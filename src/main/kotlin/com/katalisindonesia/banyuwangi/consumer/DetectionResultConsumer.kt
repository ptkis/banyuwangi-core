package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.Annotation
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import com.katalisindonesia.banyuwangi.repo.AnnotationRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotCountRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotRepo
import mu.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

private val log = KotlinLogging.logger { }

@Service
class DetectionResultConsumer(
    private val annotationRepo: AnnotationRepo,
    private val snapshotRepo: SnapshotRepo,
    private val snapshotCountRepo: SnapshotCountRepo,

    transactionManager: PlatformTransactionManager,
) {
    private val tt = TransactionTemplate(transactionManager)
    private val helper = DetectionTypeHelper()

    @RabbitListener(
        queues = [
            "#{detectionResultQueue.name}"
        ]
    )
    fun result(response: DetectionResponse) {
        try {
            tt.execute {
                val snapshotOpt = snapshotRepo.getWithImageId(response.request.uuid)
                if (snapshotOpt.isEmpty) {
                    log.info { "Cannot found snapshot with image id ${response.request.uuid}" }
                    return@execute
                }

                var count = 0
                val countMap = mutableMapOf<DetectionType, SnapshotCount>()
                for (detection in response.response) {
                    val boundingBox = detection.boundingBox ?: continue
                    val className = detection.className ?: continue
                    val probability = detection.probability ?: continue
                    val type = helper.map[className] ?: continue
                    val snapshot = snapshotOpt.get()
                    annotationRepo.save(
                        Annotation(
                            snapshot = snapshot,
                            snapshotCreated = snapshot.created,
                            snapshotImageId = snapshot.imageId,
                            name = className,
                            boundingBox = boundingBox.toModel(),
                            confidence = probability,
                            type = type,
                        )
                    )
                    count++
                    for (type1 in DetectionType.values()) {
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
                        objCount.value += 1
                    }
                }
                snapshotCountRepo.saveAll(countMap.values)

                log.info { "Saving $count detections for ${response.request.uuid}" }

                snapshotRepo.flush()
                snapshotCountRepo.flush()
            }
        } catch (expected: Exception) {
            log.info(expected) { "Cannot process snapshot ${response.request.uuid}" }
        }
    }
}

fun BoundingBox.toModel() = com.katalisindonesia.banyuwangi.model.BoundingBox(
    x = this.corners.minOf { it.x ?: 0.0 },
    y = this.corners.minOf { it.y ?: 0.0 },
    height = this.height ?: 0.0,
    width = this.width ?: 0.0,
)
