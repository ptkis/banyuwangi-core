package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.Annotation
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import com.katalisindonesia.banyuwangi.repo.AnnotationRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotCountRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotRepo
import mu.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

private val log = KotlinLogging.logger { }

@Service
class DetectionResultConsumer(
    private val annotationRepo: AnnotationRepo,
    private val snapshotRepo: SnapshotRepo,
    private val snapshotCountRepo: SnapshotCountRepo,
    private val rabbitTemplate: RabbitTemplate,
    private val messagingProperties: MessagingProperties,
    private val appProperties: AppProperties,

    transactionManager: PlatformTransactionManager,
) {
    private val tt = TransactionTemplate(transactionManager)
    private val helper = DetectionTypeHelper(appProperties)
    private val snapshotCountZeroHelper = SnapshotCountZeroHelper()

    @RabbitListener(
        queues = [
            "#{detectionResultQueue.name}"
        ],
        concurrency = "\${dashboard.messaging.detectionResultQueue.concurrency}",
    )
    fun result(response: DetectionResponse) {
        try {
            val counts1 = snapshotCounts(response) ?: emptyList()
            rabbitTemplate.convertAndSend(messagingProperties.totalQueue, counts1.toList()) {
                it.messageProperties.expiration = "${messagingProperties.totalTtl}"
                it
            }
            rabbitTemplate.convertAndSend(messagingProperties.triggerQueue, counts1.toList()) {
                it.messageProperties.expiration = "${messagingProperties.triggerTtl}"
                it
            }
        } catch (expected: Exception) {
            log.info(expected) { "Cannot process snapshot ${response.request.uuid}" }
        }
    }

    private fun snapshotCounts(response: DetectionResponse) = tt.execute {
        val snapshotOpt = snapshotRepo.getWithImageId(response.request.uuid)
        if (snapshotOpt.isEmpty) {
            log.info { "Cannot found snapshot with image id ${response.request.uuid}" }
            return@execute emptyList()
        }

        var count = 0
        val countMap = mutableMapOf<DetectionType, SnapshotCount>()
        val snapshot = snapshotOpt.get()
        for (detection in response.response) {
            val boundingBox = detection.boundingBox
            val className = detection.className ?: ""
            val probability = detection.probability ?: 0.0
            val deduction = helper.deduce(className)
            val type = deduction?.type

            val detectionMinConfidence = appProperties.detectionMinConfidences[type]
                ?: appProperties.detectionMinConfidence
            if (boundingBox == null || type == null || probability < detectionMinConfidence
            ) {
                log.debug {
                    "Discarding annotation of $type ${snapshot.camera.name} " +
                        "because of either bounding box or type is null ($className -> $type) or too " +
                        "low confidence $probability < $detectionMinConfidence"
                }
                continue
            }

            if (!snapshot.camera.isDetecting(type)) {
                log.debug { "Discarding $type annotation of ${snapshot.camera.name} because it is not enabled" }
                continue
            }
            annotationRepo.saveAndFlush(
                Annotation(
                    snapshot = snapshot,
                    snapshotCreated = snapshot.created,
                    snapshotImageId = snapshot.imageId,
                    name = className,
                    boundingBox = boundingBox.toModel(),
                    confidence = probability,
                    type = type,
                    value = deduction.value,
                )
            )
            count++
            for (type1 in DetectionType.values().filter { snapshot.camera.isDetecting(it) }) {
                countMap.getOrPut(
                    type1
                ) {
                    SnapshotCount(
                        snapshot = snapshot,
                        snapshotCreated = snapshot.created,
                        snapshotImageId = snapshot.imageId,
                        snapshotCameraName = snapshot.camera.name,
                        snapshotCameraLocation = snapshot.camera.location,
                        type = type1,
                        value = 0,
                    )
                }
            }

            countMap[type]?.let { objCount ->
                objCount.value += deduction.value
            }
        }
        snapshotCountZeroHelper.removeZeros(
            cameraId = snapshot.camera.id,
            map = countMap,
            delaySeconds = appProperties.snapshotCountZeroDelaySeconds,
        )
        val counts = countMap.values
        snapshotCountRepo.saveAll(counts)

        log.debug { "Saving $count detections for ${response.request.uuid}" }

        snapshotRepo.flush()
        snapshotCountRepo.flush()

        return@execute counts
    }
}

fun BoundingBox.toModel() = com.katalisindonesia.banyuwangi.model.BoundingBox(
    x = this.corners.minOf { it.x ?: 0.0 },
    y = this.corners.minOf { it.y ?: 0.0 },
    height = this.height ?: 0.0,
    width = this.width ?: 0.0,
)
