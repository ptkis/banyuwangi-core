package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.Snapshot
import com.katalisindonesia.banyuwangi.repo.AnnotationRepo
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotCountRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotRepo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID

@ExtendWith(SpringExtension::class)
@SpringBootTest
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
) {
    @BeforeEach
    @AfterEach
    fun cleanup() {
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
        assertEquals(DetectionType.CROWD, annotations[0].type)
    }
    @Test
    fun `single response traffic`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
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
}
