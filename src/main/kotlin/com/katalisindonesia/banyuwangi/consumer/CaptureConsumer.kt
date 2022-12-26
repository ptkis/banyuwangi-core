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
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.time.Instant

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
    private val tt = TransactionTemplate(transactionManager)

    @RabbitListener(
        queues = [
            "#{captureQueue.name}"
        ]
    )
    fun onCapture(request: CaptureRequest) {
        doOnCapture(request)
    }

    fun doOnCapture(request: CaptureRequest): Boolean {
        val nextCaptureAfterErrorInstant = request.cameraInterior.nextCaptureAfterErrorInstant
        if (nextCaptureAfterErrorInstant != null && nextCaptureAfterErrorInstant.isAfter(Instant.now())) {
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
                    cameraRepo.saveAndFlush(camera)

                    snapshotRepo.saveAndFlush(
                        Snapshot(
                            imageId = uuid,
                            camera = camera,
                            length = bytes.size.toLong(),
                        )
                    )
                }
                log.debug { "Saved snapshot with image id $uuid" }
                rabbitTemplate.convertAndSend(
                    messagingProperties.detectionQueue,
                    DetectionRequest(
                        uuid = uuid,
                        imageUri = storageService.uri(uuid).toString(),
                        callbackQueue = messagingProperties.detectionResultQueue,
                        dataset = Dataset(
                            coco = camera1.isCrowd || camera1.isTraffic,
                            streetvendor = camera1.isStreetvendor,
                            garbage = camera1.isTrash,
                            flood = camera1.isFlood,
                        )
                    )
                )
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
