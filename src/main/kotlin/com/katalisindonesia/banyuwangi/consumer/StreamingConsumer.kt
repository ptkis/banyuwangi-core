package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.CameraInterior
import com.katalisindonesia.banyuwangi.model.CameraType
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import com.katalisindonesia.banyuwangi.streaming.StreamingRest
import com.katalisindonesia.banyuwangi.util.extractGreatestDateTimeM3u8
import mu.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.UrlResource
import org.springframework.data.domain.Pageable
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import java.io.InputStreamReader
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.xml.bind.DatatypeConverter

private val log = KotlinLogging.logger { }

@Service
class StreamingConsumer(
    @Autowired
    private val cameraRepo: CameraRepo,
    @Autowired
    private val streamingRest: StreamingRest,

    @Value("\${streaming.baseUrl}")
    private val streamingBaseUrl: String,
    @Value("\${streaming.streamingToken}")
    private val streamingToken: String,

    @Value("\${cctv-analytics.streaming.maxDiffSeconds:3600}")
    private val maxDiffSeconds: Long,
    transactionManager: PlatformTransactionManager,
) {
    private val tt = TransactionTemplate(
        transactionManager,
        txDef(
            name = "Streaming",
            isolationLevel = TransactionDefinition.ISOLATION_REPEATABLE_READ,
        )
    )

    @RabbitListener(
        queues = [
            "#{streamingCheckQueue.name}"
        ],
        concurrency = "\${dashboard.messaging.streamingCheckQueue.concurrency}",
    )
    @Retryable
    fun check() {
        try {
            doCheck()
        } catch (expected: Exception) {
            log.error(expected) { "Error while doing schedule check" }
        }
    }

    @Retryable
    fun doCheck() {
        log.info { "Begin StreamingConsumer.check" }
        var countInit = 0
        var countCheck = 0
        cameraRepo.findWithIsActive(true, Pageable.unpaged()).forEach { camera: Camera ->
            tt.execute {
                if (initCameraUrl(camera)) {
                    countInit++
                }
                if (doCheckStreamCamera(camera)) {
                    countCheck++
                }
            }
        }
        log.info {
            "Finish StreamingConsumer.check " +
                "with " +
                "$countInit camera(s) " +
                "initialized " +
                "and " +
                "$countCheck camera(s) " +
                "online checked"
        }
    }

    private fun doCheckStreamCamera(camera1: Camera): Boolean {
        try {
            val camera = cameraRepo.getReferenceById(camera1.id)
            var modified = false

            val interior = camera.interior ?: CameraInterior()
            camera.interior = interior
            val url = interior.liveViewUrl ?: return false
            val res = UrlResource(url)

            val content = InputStreamReader(res.inputStream).use { it.readText() }
            val dt = extractGreatestDateTimeM3u8(content)

            val oldLoginSucceeded = interior.isLoginSucceeded
            interior.isLoginSucceeded =
                dt != null && ChronoUnit.SECONDS.between(dt, LocalDateTime.now()) <= maxDiffSeconds
            if (oldLoginSucceeded != interior.isLoginSucceeded) {
                cameraRepo.saveAndFlush(camera)
                modified = true
            }

            return modified
        } catch (expected: Exception) {
            log.debug(expected) { "Cannot check camera ${camera1.name}" }
            return false
        }
    }

    private fun initCameraUrl(camera1: Camera): Boolean {
        try {
            val camera = cameraRepo.getReferenceById(camera1.id)
            var modified = false
            val cameraUrl =
                streamingBaseUrl +
                    "/$streamingToken/" +
                    camera.userName +
                    "/${camera.password}" +
                    "/${camera.host}" +
                    "/${camera.rtspPort}" +
                    "/${camera.channel}" +
                    "/web_port" +
                    "/${camera.httpPort}"
            val hash = calcMd5Hash(cameraUrl)

            val interior = camera.interior ?: CameraInterior()
            camera.interior = interior
            if (interior.liveViewUrl == null || hash != interior.liveViewHash) {
                val cameraStreamingUrl = getCameraStreaming(camera) ?: return modified
                interior.liveViewUrl = streamingBaseUrl + cameraStreamingUrl
                interior.liveViewHash = hash
                cameraRepo.saveAndFlush(camera)

                modified = true
                log.info("curl -d \"\" $cameraUrl/html/true")
            }

            return modified
        } catch (expected: Exception) {
            log.info(expected) { "Cannot init camera ${camera1.name}" }
            return false
        }
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun calcMd5Hash(newValue: String): String {
        val md = MessageDigest.getInstance("MD5")
        md.update(newValue.toByteArray())
        val digest = md.digest()
        return DatatypeConverter.printHexBinary(digest).uppercase()
    }

    private fun getCameraStreaming(camera: Camera): String? {
        return streamingRest.getCameraUrl(
            user = camera.userName,
            password = camera.password,
            host = camera.host,
            port = camera.rtspPort,
            channel = camera.channel,
            html = false,
            type = camera.type ?: CameraType.HIKVISION,
            webPort = camera.httpPort
        ).execute().body()
    }
}
