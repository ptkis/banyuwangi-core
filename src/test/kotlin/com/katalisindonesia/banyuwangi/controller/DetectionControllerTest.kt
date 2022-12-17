package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.consumer.BoundingBox
import com.katalisindonesia.banyuwangi.consumer.Corners
import com.katalisindonesia.banyuwangi.consumer.Detection
import com.katalisindonesia.banyuwangi.consumer.DetectionRequest
import com.katalisindonesia.banyuwangi.consumer.DetectionResponse
import com.katalisindonesia.banyuwangi.consumer.DetectionResultConsumer
import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.Snapshot
import com.katalisindonesia.banyuwangi.repo.AnnotationRepo
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotCountRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotRepo
import com.katalisindonesia.banyuwangi.security.TokenManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.UUID

/**
 * Integration test for uploading to OBS.
 *
 * @author Thomas Edwin Santosa
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DetectionControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val tokenManager: TokenManager,
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
    private val detectionController: DetectionController,

) {
    @BeforeEach
    @AfterEach
    fun cleanup() {
        snapshotCountRepo.deleteAll()
        annotationRepo.deleteAll()
        snapshotRepo.deleteAll()
        cameraRepo.deleteAll()
        detectionController.resetProductionMode()
    }

    @Test
    fun `browse without token should redirect`() {
        mockMvc.get("/v1/detection/browse").andExpect {
            status {
                is3xxRedirection()
            }
        }
    }

    private fun token(): String = tokenManager.accessToken("banyuwangi-test", "banyuwangi-test")

    @Test
    fun `browse with token`() {

        val token = token()

        mockMvc.get("/v1/detection/browse") {
            headers {
                setBearerAuth(token)
            }
        }.andExpect {
            status {
                isOk()
            }
            content {
                json("""{ "success": true}""")
            }
        }
    }
    @Test
    fun `browse with token with size 1`() {

        val token = token()

        mockMvc.get("/v1/detection/browse?size=1") {
            headers {
                setBearerAuth(token)
            }
        }.andExpect {
            status {
                isOk()
            }
            content {
                json(
                    """{
  "success": true,
  "message": "ok",
  "data": {
    "content": [
      {
        "cameraName": "Camera 1",
        "value": 3,
        "imageSrc": "https://image.com:443/v1/image/14f0de44-7200-4b81-9197-98b26455b36e",
        "annotations": [
          {
            "snapshotImageId": "14f0de44-7200-4b81-9197-98b26455b36e",
            "name": "dog",
            "boundingBox": {
              "x": 0.162,
              "y": 0.357,
              "width": 0.25,
              "height": 0.545
            },
            "confidence": 0.96922
          },
          {
            "snapshotImageId": "14f0de44-7200-4b81-9197-98b26455b36e",
            "name": "bicycle",
            "boundingBox": {
              "x": 0.152,
              "y": 0.249,
              "width": 0.558,
              "height": 0.57
            },
            "confidence": 0.66656
          },
          {
            "snapshotImageId": "14f0de44-7200-4b81-9197-98b26455b36e",
            "name": "truck",
            "boundingBox": {
              "x": 0.61,
              "y": 0.131,
              "width": 0.167,
              "height": 0.284
            },
            "confidence": 0.62682
          }
        ]
      }
    ],
    "pageable": {
      "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 1,
      "paged": true,
      "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 1,
    "first": true,
    "size": 1,
    "number": 0,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "numberOfElements": 1,
    "empty": false
  }
}"""
                )
            }
        }
    }

    @Test
    fun `browse with toke and actual data`() {
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

        val token = token()

        mockMvc.get("/v1/detection/browse") {
            headers {
                setBearerAuth(token)
            }
        }.andExpect {
            status {
                isOk()
            }
            content {
                json(
                    """{
  "success": true,
  "message": "ok",
  "data": {
    "content": [
      {
        "location": "01",
        "cameraName": "Test 01",
        "type": "CROWD",
        "value": 1,
        "annotations": [
          {
            "snapshotCameraLocation": "01",
            "name": "person",
            "type": "CROWD",
            "boundingBox": {
              "x": 0.0,
              "y": 0.0,
              "width": 1.0,
              "height": 1.0
            },
            "confidence": 0.8
          }
        ]
      },
      {
        "location": "01",
        "cameraName": "Test 01",
        "type": "FLOOD",
        "value": 0,
        "annotations": []
      },
      {
        "location": "01",
        "cameraName": "Test 01",
        "type": "STREETVENDOR",
        "value": 0,
        "annotations": []
      },
      {
        "location": "01",
        "cameraName": "Test 01",
        "type": "TRAFFIC",
        "value": 0,
        "annotations": []
      },
      {
        "location": "01",
        "cameraName": "Test 01",
        "type": "TRASH",
        "value": 0,
        "annotations": []
      }
    ],
    "pageable": {
      "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 1000,
      "paged": true,
      "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 5,
    "first": true,
    "size": 1000,
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "numberOfElements": 5,
    "empty": false
  }
}"""
                )
            }
        }
    }
    @Test
    fun `browse with toke and actual data full filter`() {
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

        val token = token()

        mockMvc.get(
            "/v1/detection/browse?type=TRAFFIC&page=0&size=9" +
                "&startDate=2022-12-15" +
                "&endDate=2022-12-16&location=DEPAN"
        ) {
            headers {
                setBearerAuth(token)
            }
        }.andExpect {
            status {
                isOk()
            }
            content {
                json(
                    """{
  "success": true,
  "message": "ok",
  "data": {
    "content": [],
    "pageable": {
      "sort": {
        "empty": false,
        "unsorted": false,
        "sorted": true
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 9,
      "paged": true,
      "unpaged": false
    },
    "last": true,
    "totalElements": 0,
    "totalPages": 0,
    "first": true,
    "size": 9,
    "number": 0,
    "sort": {
      "empty": false,
      "unsorted": false,
      "sorted": true
    },
    "numberOfElements": 0,
    "empty": true
  }
}"""
                )
            }
        }
    }
}
