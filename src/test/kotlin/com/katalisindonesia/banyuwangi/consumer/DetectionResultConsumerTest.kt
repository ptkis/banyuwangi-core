package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.AlarmSetting
import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.Snapshot
import com.katalisindonesia.banyuwangi.repo.AlarmRepo
import com.katalisindonesia.banyuwangi.repo.AnnotationRepo
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotCountRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotRepo
import com.katalisindonesia.banyuwangi.repo.TotalRepo
import com.katalisindonesia.banyuwangi.task.DeleteImageTask
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("default", "secret")
class DetectionResultConsumerTest(
    @Autowired
    private val detectionResultConsumer: DetectionResultConsumer,

    @Autowired
    private val snapshotRepo: SnapshotRepo,

    @Autowired
    private val snapshotCountRepo: SnapshotCountRepo,

    @Autowired
    private val cameraRepo: CameraRepo,

    @Autowired
    private val annotationRepo: AnnotationRepo,

    @Autowired
    private val alarmRepo: AlarmRepo,

    @Autowired
    private val totalRepo: TotalRepo,

    @Autowired
    private val rabbitAdmin: RabbitAdmin,

    @Autowired
    private val messagingProperties: MessagingProperties,

    @Autowired
    private val deleteImageTask: DeleteImageTask,
) {
    @BeforeEach
    @AfterEach
    fun cleanup() {
        rabbitAdmin.purgeQueue(messagingProperties.triggerQueue, false)
        rabbitAdmin.purgeQueue(messagingProperties.totalQueue, false)

        totalRepo.deleteAll()
        alarmRepo.deleteAll()
        snapshotCountRepo.deleteAll()
        annotationRepo.deleteAll()
        snapshotRepo.deleteAll()
        cameraRepo.deleteAll()
    }

    @Test
    fun `not success`() {
        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = false,
                    message = "error",
                    response = emptyList(),
                    request = DetectionRequest(
                        uuid = UUID.randomUUID(),
                        imageUri = "http://invalid",
                        callbackQueue = "/queue"
                    )
                )
            )
        }
    }

    @Test
    fun `empty response`() {
        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = emptyList(),
                    request = DetectionRequest(
                        uuid = UUID.randomUUID(),
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }
    }

    @Test
    fun `single response crowd`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            isCrowd = true,
        )
        cameraRepo.saveAndFlush(camera)

        val snapshot = Snapshot(
            imageId = UUID.randomUUID(),
            camera = camera,
            length = 1000000,
        )
        snapshotRepo.saveAndFlush(snapshot)

        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = listOf(
                        Detection(
                            boundingBox = BoundingBox(
                                corners = listOf(
                                    Corners(
                                        x = 0.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 1.0
                                    ),
                                    Corners(
                                        x = 0.0,
                                        y = 1.0
                                    ),
                                ),
                                width = 1.0,
                                height = 1.0,
                            ),
                            className = "person",
                            probability = 0.8,
                        )
                    ),
                    request = DetectionRequest(
                        uuid = snapshot.imageId,
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }

        val annotations = annotationRepo.findAll()
        assertEquals(1, annotations.size)
        assertEquals(DetectionType.CROWD, annotations[0].type)
    }
    @Test
    fun `single response traffic`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            isTraffic = true,
        )
        cameraRepo.saveAndFlush(camera)

        val snapshot = Snapshot(
            imageId = UUID.randomUUID(),
            camera = camera,
            length = 1000000,
        )
        snapshotRepo.saveAndFlush(snapshot)

        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = listOf(
                        Detection(
                            boundingBox = BoundingBox(
                                corners = listOf(
                                    Corners(
                                        x = 0.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 1.0
                                    ),
                                    Corners(
                                        x = 0.0,
                                        y = 1.0
                                    ),
                                ),
                                width = 1.0,
                                height = 1.0,
                            ),
                            className = "car",
                            probability = 0.8
                        )
                    ),
                    request = DetectionRequest(
                        uuid = snapshot.imageId,
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }

        val annotations = annotationRepo.findAll()
        assertEquals(1, annotations.size)
        assertEquals(DetectionType.TRAFFIC, annotations[0].type)
    }
    @Test
    fun `single response not relevant`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            isTraffic = true,
            isCrowd = true,
            isStreetvendor = true,
            isTrash = true,
            isFlood = true,
        )
        cameraRepo.saveAndFlush(camera)

        val snapshot = Snapshot(
            imageId = UUID.randomUUID(),
            camera = camera,
            length = 1000000,
        )
        snapshotRepo.saveAndFlush(snapshot)

        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = listOf(
                        Detection(
                            boundingBox = BoundingBox(
                                corners = listOf(
                                    Corners(
                                        x = 0.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 1.0
                                    ),
                                    Corners(
                                        x = 0.0,
                                        y = 1.0
                                    ),
                                ),
                                width = 1.0,
                                height = 1.0,
                            ),
                            className = "airplane",
                            probability = 0.8
                        )
                    ),
                    request = DetectionRequest(
                        uuid = snapshot.imageId,
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }

        val annotations = annotationRepo.findAll()
        assertEquals(0, annotations.size)
    }

    @Test
    fun `single response traffic alarm`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            alarmSetting = AlarmSetting(maxTraffic = 0),
            isTraffic = true,
        )
        cameraRepo.saveAndFlush(camera)

        val snapshot = Snapshot(
            imageId = UUID.randomUUID(),
            camera = camera,
            length = 1000000,
        )
        snapshotRepo.saveAndFlush(snapshot)

        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = listOf(
                        Detection(
                            boundingBox = BoundingBox(
                                corners = listOf(
                                    Corners(
                                        x = 0.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 1.0
                                    ),
                                    Corners(
                                        x = 0.0,
                                        y = 1.0
                                    ),
                                ),
                                width = 1.0,
                                height = 1.0,
                            ),
                            className = "car",
                            probability = 0.8
                        )
                    ),
                    request = DetectionRequest(
                        uuid = snapshot.imageId,
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }

        val annotations = annotationRepo.findAll()
        assertEquals(1, annotations.size)
        assertEquals(DetectionType.TRAFFIC, annotations[0].type)

        Thread.sleep(1000)
        assertEquals(1, alarmRepo.count())

        val alarm = alarmRepo.findAll()[0]

        assertEquals(0, alarm.maxValue)
        assertEquals(1, alarm.snapshotCount.value)
    }

    @Test
    fun `single response traffic no alarm`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            alarmSetting = AlarmSetting(maxTraffic = 1),
            isTraffic = true,
        )
        cameraRepo.saveAndFlush(camera)

        val snapshot = Snapshot(
            imageId = UUID.randomUUID(),
            camera = camera,
            length = 1000000,
        )
        snapshotRepo.saveAndFlush(snapshot)

        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = listOf(
                        Detection(
                            boundingBox = BoundingBox(
                                corners = listOf(
                                    Corners(
                                        x = 0.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 1.0
                                    ),
                                    Corners(
                                        x = 0.0,
                                        y = 1.0
                                    ),
                                ),
                                width = 1.0,
                                height = 1.0,
                            ),
                            className = "car",
                            probability = 0.8
                        )
                    ),
                    request = DetectionRequest(
                        uuid = snapshot.imageId,
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }

        val annotations = annotationRepo.findAll()
        assertEquals(1, annotations.size)
        assertEquals(DetectionType.TRAFFIC, annotations[0].type)

        Thread.sleep(1000)
        assertEquals(0, alarmRepo.count())
    }

    @Test
    fun `single response streetvendor alarm`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            alarmSetting = AlarmSetting(maxStreetvendor = 0),
            isStreetvendor = true,
        )
        cameraRepo.saveAndFlush(camera)

        val snapshot = Snapshot(
            imageId = UUID.randomUUID(),
            camera = camera,
            length = 1000000,
        )
        snapshotRepo.saveAndFlush(snapshot)

        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = listOf(
                        Detection(
                            boundingBox = BoundingBox(
                                corners = listOf(
                                    Corners(
                                        x = 0.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 1.0
                                    ),
                                    Corners(
                                        x = 0.0,
                                        y = 1.0
                                    ),
                                ),
                                width = 1.0,
                                height = 1.0,
                            ),
                            className = "streetvendor",
                            probability = 0.8
                        )
                    ),
                    request = DetectionRequest(
                        uuid = snapshot.imageId,
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }

        val annotations = annotationRepo.findAll()
        assertEquals(1, annotations.size)
        assertEquals(DetectionType.STREETVENDOR, annotations[0].type)

        Thread.sleep(1000)
        assertEquals(1, alarmRepo.count())

        val alarm = alarmRepo.findAll()[0]

        assertEquals(0, alarm.maxValue)
        assertEquals(1, alarm.snapshotCount.value)
    }

    @Test
    fun `single response streetvendor no alarm`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            alarmSetting = AlarmSetting(maxStreetvendor = 1),
            isStreetvendor = true,
        )
        cameraRepo.saveAndFlush(camera)

        val snapshot = Snapshot(
            imageId = UUID.randomUUID(),
            camera = camera,
            length = 1000000,
        )
        snapshotRepo.saveAndFlush(snapshot)

        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = listOf(
                        Detection(
                            boundingBox = BoundingBox(
                                corners = listOf(
                                    Corners(
                                        x = 0.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 1.0
                                    ),
                                    Corners(
                                        x = 0.0,
                                        y = 1.0
                                    ),
                                ),
                                width = 1.0,
                                height = 1.0,
                            ),
                            className = "streetvendor",
                            probability = 0.8
                        )
                    ),
                    request = DetectionRequest(
                        uuid = snapshot.imageId,
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }

        val annotations = annotationRepo.findAll()
        assertEquals(1, annotations.size)
        assertEquals(DetectionType.STREETVENDOR, annotations[0].type)

        Thread.sleep(1000)
        assertEquals(0, alarmRepo.count())
    }
    @Test
    fun `single response flood alarm`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            alarmSetting = AlarmSetting(maxFlood = 0),
            isFlood = true,
        )
        cameraRepo.saveAndFlush(camera)

        val snapshot = Snapshot(
            imageId = UUID.randomUUID(),
            camera = camera,
            length = 1000000,
        )
        snapshotRepo.saveAndFlush(snapshot)

        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = listOf(
                        Detection(
                            boundingBox = BoundingBox(
                                corners = listOf(
                                    Corners(
                                        x = 0.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 1.0
                                    ),
                                    Corners(
                                        x = 0.0,
                                        y = 1.0
                                    ),
                                ),
                                width = 1.0,
                                height = 1.0,
                            ),
                            className = "flood-puddle",
                            probability = 0.8
                        )
                    ),
                    request = DetectionRequest(
                        uuid = snapshot.imageId,
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }

        val annotations = annotationRepo.findAll()
        assertEquals(1, annotations.size)
        assertEquals(DetectionType.FLOOD, annotations[0].type)

        Thread.sleep(1000)
        assertEquals(1, alarmRepo.count())

        val alarm = alarmRepo.findAll()[0]

        assertEquals(0, alarm.maxValue)
        assertEquals(1, alarm.snapshotCount.value)
    }

    @Test
    fun `single response flood no alarm`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            alarmSetting = AlarmSetting(maxFlood = 1),
            isFlood = true,
        )
        cameraRepo.saveAndFlush(camera)

        val snapshot = Snapshot(
            imageId = UUID.randomUUID(),
            camera = camera,
            length = 1000000,
        )
        snapshotRepo.saveAndFlush(snapshot)

        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = listOf(
                        Detection(
                            boundingBox = BoundingBox(
                                corners = listOf(
                                    Corners(
                                        x = 0.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 1.0
                                    ),
                                    Corners(
                                        x = 0.0,
                                        y = 1.0
                                    ),
                                ),
                                width = 1.0,
                                height = 1.0,
                            ),
                            className = "flood-puddle",
                            probability = 0.8
                        )
                    ),
                    request = DetectionRequest(
                        uuid = snapshot.imageId,
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }

        val annotations = annotationRepo.findAll()
        assertEquals(1, annotations.size)
        assertEquals(DetectionType.FLOOD, annotations[0].type)

        Thread.sleep(1000)
        assertEquals(0, alarmRepo.count())
    }
    @Test
    fun `single response garbage alarm`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            alarmSetting = AlarmSetting(maxTrash = 0),
            isTrash = true,
            isCrowd = true,
            isTraffic = true,
            isFlood = true,
            isStreetvendor = true,
        )
        cameraRepo.saveAndFlush(camera)

        val snapshot = Snapshot(
            imageId = UUID.randomUUID(),
            camera = camera,
            length = 1000000,
        )
        snapshotRepo.saveAndFlush(snapshot)

        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = listOf(
                        Detection(
                            boundingBox = BoundingBox(
                                corners = listOf(
                                    Corners(
                                        x = 0.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 1.0
                                    ),
                                    Corners(
                                        x = 0.0,
                                        y = 1.0
                                    ),
                                ),
                                width = 1.0,
                                height = 1.0,
                            ),
                            className = "garbage-metal",
                            probability = 0.8
                        )
                    ),
                    request = DetectionRequest(
                        uuid = snapshot.imageId,
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }

        val annotations = annotationRepo.findAll()
        assertEquals(1, annotations.size)
        assertEquals(DetectionType.TRASH, annotations[0].type)

        Thread.sleep(1000)
        assertEquals(1, alarmRepo.count())

        val alarm = alarmRepo.findAll()[0]

        assertEquals(0, alarm.maxValue)
        assertEquals(1, alarm.snapshotCount.value)
        val totals = totalRepo.findAll()
        assertEquals(5, totals.size)
        for (total in totals) {
            if (total.type == DetectionType.TRASH) {
                assertEquals(1L, total.countAlarmValue)
                assertEquals(1L, total.countValue)
                assertEquals(1L, total.maxValue)
                assertEquals(1L, total.sumValue)
                assertEquals(1L, total.avgValue)
            } else {
                assertEquals(0L, total.countAlarmValue)
                assertEquals(0L, total.countValue)
                assertEquals(0L, total.maxValue)
                assertEquals(0L, total.sumValue)
                assertEquals(0L, total.avgValue)
            }
        }
    }

    @Test
    fun `single response garbage no alarm`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            alarmSetting = AlarmSetting(maxTrash = 1),
            isTrash = true,
            isCrowd = true,
            isTraffic = true,
            isFlood = true,
            isStreetvendor = true,
        )
        cameraRepo.saveAndFlush(camera)

        val snapshot = Snapshot(
            imageId = UUID.randomUUID(),
            camera = camera,
            length = 1000000,
        )
        snapshotRepo.saveAndFlush(snapshot)

        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = listOf(
                        Detection(
                            boundingBox = BoundingBox(
                                corners = listOf(
                                    Corners(
                                        x = 0.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 1.0
                                    ),
                                    Corners(
                                        x = 0.0,
                                        y = 1.0
                                    ),
                                ),
                                width = 1.0,
                                height = 1.0,
                            ),
                            className = "garbage-metal",
                            probability = 0.8
                        )
                    ),
                    request = DetectionRequest(
                        uuid = snapshot.imageId,
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }

        val annotations = annotationRepo.findAll()
        assertEquals(1, annotations.size)
        assertEquals(DetectionType.TRASH, annotations[0].type)

        Thread.sleep(1000)
        assertEquals(0, alarmRepo.count())

        assertEquals(
            0L,
            deleteImageTask.doDelete(
                minFreeSpace = 0L,
            ).deletedBytes,
            "should not remove if minFreeSpace is zero",
        )
        assertNotEquals(
            0L,
            deleteImageTask.doDelete(
                minFreeSpace = Long.MAX_VALUE,
            ).deletedBytes,
            "should remove if minFreeSpace is not zero",
        )
        val totals = totalRepo.findAll()
        assertEquals(5, totals.size)
        for (total in totals) {
            if (total.type == DetectionType.TRASH) {
                assertEquals(0L, total.countAlarmValue)
                assertEquals(1L, total.countValue)
                assertEquals(1L, total.maxValue)
                assertEquals(1L, total.sumValue)
                assertEquals(1L, total.avgValue)
            } else {
                assertEquals(0L, total.countAlarmValue)
                assertEquals(0L, total.countValue)
                assertEquals(0L, total.maxValue)
                assertEquals(0L, total.sumValue)
                assertEquals(0L, total.avgValue)
            }
        }
    }
    @Test
    fun `single response garbage no detection in camera`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            alarmSetting = AlarmSetting(maxTrash = 1),
            isTrash = false,
        )
        cameraRepo.saveAndFlush(camera)

        val snapshot = Snapshot(
            imageId = UUID.randomUUID(),
            camera = camera,
            length = 1000000,
        )
        snapshotRepo.saveAndFlush(snapshot)

        assertDoesNotThrow {
            detectionResultConsumer.result(
                DetectionResponse(
                    success = true,
                    message = "ok",
                    response = listOf(
                        Detection(
                            boundingBox = BoundingBox(
                                corners = listOf(
                                    Corners(
                                        x = 0.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 0.0
                                    ),
                                    Corners(
                                        x = 1.0,
                                        y = 1.0
                                    ),
                                    Corners(
                                        x = 0.0,
                                        y = 1.0
                                    ),
                                ),
                                width = 1.0,
                                height = 1.0,
                            ),
                            className = "garbage-metal",
                            probability = 0.8
                        )
                    ),
                    request = DetectionRequest(
                        uuid = snapshot.imageId,
                        imageUri = "http://someimage",
                        callbackQueue = "/queue"
                    )
                )
            )
        }

        val annotations = annotationRepo.findAll()
        assertEquals(0, annotations.size)

        Thread.sleep(1000)
        assertEquals(0, alarmRepo.count())

        val totals = totalRepo.findAll()
        assertEquals(0, totals.size)
    }
}
