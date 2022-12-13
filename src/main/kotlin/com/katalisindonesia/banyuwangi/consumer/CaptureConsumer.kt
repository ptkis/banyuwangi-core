package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.CameraType
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

private val log = KotlinLogging.logger { }
@Service
class CaptureConsumer(
    private val rabbitTemplate: RabbitTemplate,
    private val messagingProperties: MessagingProperties,
    private val storageService: StorageService,
    private val snapshotRepo: SnapshotRepo,
    private val cameraRepo: CameraRepo,
    private val captureService: CaptureService,
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
        val operation = when (request.camera.type) {
            CameraType.HIKVISION -> captureService::hikvision
            // CameraType.ONVIF -> captureService::onvif
            else -> captureService::empty
        }
        val bytesOpt = operation.invoke(request.camera)

        if (bytesOpt.isPresent) {
            val bytes = bytesOpt.get()
            val uuid = storageService.store(bytes)
            tt.execute {
                snapshotRepo.saveAndFlush(
                    Snapshot(
                        imageId = uuid,
                        camera = cameraRepo.getReferenceById(request.camera.id),
                        length = bytes.size.toLong(),
                    )
                )
            }
            log.debug { "Saved snapshot with image id $uuid" }
            rabbitTemplate.convertAndSend(
                messagingProperties.detectionQueue,
                DetectionRequest(
                    imageUri = storageService.uri(uuid).toString(),
                    callbackQueue = messagingProperties.detectionResultQueue,
                )
            )
            return true
        } else {
            log.debug { "Cannot get snapshot from ${request.camera.name}" }
        }
        return false
    }
}
