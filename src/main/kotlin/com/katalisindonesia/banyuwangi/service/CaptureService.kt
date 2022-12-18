package com.katalisindonesia.banyuwangi.service

import com.burgstaller.okhttp.digest.Credentials
import com.burgstaller.okhttp.digest.DigestAuthenticator
import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.Camera
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.lang.RuntimeException
import java.util.Optional
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

private val log = KotlinLogging.logger { }

@Service
class CaptureService(
    private val appProperties: AppProperties,
) {
/*
     suspend fun onvif(camera: Camera): Optional<ByteArray> {
        val device = OnvifDevice(
            "http://${camera.host}:${camera.httpPort}",
            camera.userName,
            camera.password
        )
        val onvifManager = OnvifManager(object : OnvifResponseListener {
            override fun onResponse(onvifDevice: OnvifDevice?, response: OnvifResponse<*>?) {
                log.debug { "device=$onvifDevice response=$response" }
            }

            override fun onError(onvifDevice: OnvifDevice?, errorCode: Int, errorMessage: String?) {
                log.error { "device=$onvifDevice errorCode=$errorCode errorMessage=$errorMessage" }
            }
        })
        val profiles = suspendCancellableCoroutine { continuation: Continuation<List<OnvifMediaProfile>> ->
            onvifManager.getMediaProfiles(
                device
            ) { _, mediaProfiles -> continuation.resume(mediaProfiles) }
        }
        log.debug { "Get ${profiles.size} from device ${camera.name}" }
        for (profile in profiles) {
            val snapshotUri = suspendCancellableCoroutine { continuation ->
                onvifManager.getSnapshotURI(
                    device, profile
                ) { _, _, uri -> continuation.resume(uri) }
            }

            log.debug { "Get snapshot uri $snapshotUri for device ${camera.name}" }

            if (snapshotUri.isNotEmpty()) {
                val bytes = withContext(Dispatchers.IO) {
                    restTemplate.getForObject(snapshotUri, ByteArray::class.java)
                }
                if (bytes != null && bytes.isNotEmpty()) {
                    return Optional.of(bytes)
                }
            }
        }
        return Optional.empty()
    }
*/

    fun hikvision(camera: Camera): Optional<ByteArray> {
        val credentials = Credentials(camera.userName, camera.password)

        val timeoutSeconds = appProperties.timeoutSeconds

        val httpClient = OkHttpClient.Builder()
            .authenticator(DigestAuthenticator(credentials))
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .callTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .build()

        return doCapture(
            "http://${camera.host}:${camera.httpPort}" +
                "/ISAPI/Streaming/channels/" +
                "${camera.channel}${camera.captureQualityChannel.orEmpty()}" +
                "/picture",
            httpClient
        )
        // http://<username>:<password>@<address>:
        // <httpport>/Streaming/channels/1/
        // picture?videoResolutionWidth=$width
        // &videoResolutionHeight=$height
    }

    private fun doCapture(
        url: String,
        httpClient: OkHttpClient,
    ): Optional<ByteArray> {
        log.info("Calling $url")

        val req = Request.Builder().url(url).build()
        val resp = httpClient.newCall(req).execute()

        resp.use { resp1: Response ->
            val body = resp1.body
            if (!resp1.isSuccessful) {
                log.info { "$url is unsuccessful: ${resp1.code}" }
                log.debug { "Body: $body" }
                log.debug { "Headers: ${resp1.headers}" }

                throw CaptureException(message = "HTTP Error ${resp1.code}")
            } else {
                val data = body?.bytes() ?: byteArrayOf()

                return if (data.isEmpty()) {
                    log.info("$url returns empty")
                    Optional.empty()
                } else {
                    val bufferedImage = ImageIO.read(ByteArrayInputStream(data))
                    val height = bufferedImage.height
                    val width = bufferedImage.width

                    log.info("$url returns ${width}x$height picture")
                    Optional.of(data)
                }
            }
        }
    }

    fun empty(camera: Camera): Optional<ByteArray> {
        log.debug { "empty: ${camera.name}" }
        return Optional.empty<ByteArray>()
    }
}

class CaptureException(message: String,) : RuntimeException(message)
