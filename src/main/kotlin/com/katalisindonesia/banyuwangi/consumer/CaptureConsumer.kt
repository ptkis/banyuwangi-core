package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.CameraInterior
import com.katalisindonesia.banyuwangi.model.CameraType
import com.katalisindonesia.banyuwangi.model.CaptureMethod
import com.katalisindonesia.banyuwangi.model.Snapshot
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotRepo
import com.katalisindonesia.banyuwangi.service.CaptureService
import com.katalisindonesia.imageserver.service.StorageService
import mu.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private val log = KotlinLogging.logger { }

@Service
class CaptureConsumer(
    private val rabbitTemplate: RabbitTemplate,
    private val messagingProperties: MessagingProperties,
    private val storageService: StorageService,
    private val snapshotRepo: SnapshotRepo,
    private val cameraRepo: CameraRepo,
    private val captureService: CaptureService,
    private val appProperties: AppProperties,
    transactionManager: PlatformTransactionManager,
) {
    private val tt = TransactionTemplate(
        transactionManager,
        txDef(
            name = "Capture",
            isolationLevel = TransactionDefinition.ISOLATION_REPEATABLE_READ,
        )
    )
    private val lastCaptureMap = ConcurrentHashMap<UUID, Instant>()

    @RabbitListener(
        queues = [
            "#{captureQueue.name}"
        ],
        concurrency = "\${dashboard.messaging.captureQueue.concurrency}",
    )
    @Retryable
    fun onCapture(request: CaptureRequest) {
        doOnCapture(request)
    }

    fun doOnCapture(request: CaptureRequest): Boolean {
        val nextCaptureAfterErrorInstant = request.cameraInterior.nextCaptureAfterErrorInstant
        if (nextCaptureAfterErrorInstant != null && nextCaptureAfterErrorInstant.isAfter(Instant.now())) {
            return false
        }
        val nextCaptureInstant = request.cameraInterior.lastCaptureInstant?.plusSeconds(
            appProperties.captureDelaySeconds
        )
        if (nextCaptureInstant != null && nextCaptureInstant.isAfter(Instant.now())) {
            return false
        }
        val lastCaptureInstant = lastCaptureMap[request.camera.id]
        if (lastCaptureInstant != null && lastCaptureInstant.plusSeconds(appProperties.captureDelaySeconds)
            .isAfter(Instant.now())
        ) {
            return false
        }
        val camera1 = request.camera
        val operation = when (camera1.type) {
            CameraType.HIKVISION -> captureService::hikvision
            // CameraType.ONVIF -> captureService::onvif
            else -> captureService::empty
        }
        try {
            val bytesOpt = operation.invoke(camera1)

            if (bytesOpt.isPresent) {
                val bytes = bytesOpt.get()
                val uuid = storageService.store(bytes)
                tt.execute {
                    val camera = cameraRepo.getReferenceById(camera1.id)
                    val interior = camera.interior ?: CameraInterior()
                    camera.interior = interior

                    interior.lastCaptureInstant = Instant.now()
                    interior.lastCaptureMethod = CaptureMethod.ISAPI
                    interior.lastCaptureErrorMessage = null
                    interior.lastCaptureErrorInstant = null
                    cameraRepo.saveAndFlush(camera)

                    snapshotRepo.saveAndFlush(
                        Snapshot(
                            imageId = uuid,
                            camera = camera,
                            length = bytes.size.toLong(),
                        )
                    )
                }
                lastCaptureMap[camera1.id] = Instant.now()
                log.debug { "Saved snapshot with image id $uuid" }
                rabbitTemplate.convertAndSend(
                    messagingProperties.detectionQueue,
                    DetectionRequest(
                        uuid = uuid,
                        imageUri = storageService.uri(uuid).toString(),
                        callbackQueue = messagingProperties.detectionResultQueue,
                        dataset = Dataset(
                            coco = false,
                            streetvendor = camera1.isStreetvendor,
                            garbage = camera1.isTrash,
                            flood = camera1.isFlood,
                            traffic = camera1.isTraffic,
                            crowd = camera1.isCrowd,
                        )
                    )
                ) {
                    it.messageProperties.expiration = "${messagingProperties.detectionTtl}"
                    it
                }
                return true
            } else {
                log.debug { "Cannot get snapshot from ${camera1.name}" }
            }
        } catch (expected: Exception) {
            handleException(expected, request)
        }
        return false
    }

    private fun handleException(expected: Exception, request: CaptureRequest) {
        log.debug(expected) { "Catch exception" }
        tt.execute {
            val cameraOpt = cameraRepo.findById(request.camera.id)
            if (cameraOpt.isPresent) {
                val camera = cameraOpt.get()
                val interior = camera.interior ?: CameraInterior()
                camera.interior = interior

                interior.lastCaptureErrorMessage = expected.message
                interior.lastCaptureErrorInstant = Instant.now()
                interior.nextCaptureAfterErrorInstant = Instant.now()
                    .plusSeconds(
                        appProperties.captureErrorBackoffSeconds
                    )

                cameraRepo.saveAndFlush(camera)
            } else {
                log.info { "Cannot found camera with id ${request.camera.id}" }
            }
        }
    }
}
