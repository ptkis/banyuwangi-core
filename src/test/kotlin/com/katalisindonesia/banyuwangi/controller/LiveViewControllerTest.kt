package com.katalisindonesia.banyuwangi.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.katalisindonesia.banyuwangi.consumer.MessagingProperties
import com.katalisindonesia.banyuwangi.consumer.StreamingConsumer
import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.CameraInterior
import com.katalisindonesia.banyuwangi.model.CameraType
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import com.katalisindonesia.banyuwangi.security.TokenManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.UUID

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("default", "secret")
@Disabled("Disable token base tests until foreign traffic to user's keycloak is unblocked")
class LiveViewControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val tokenManager: TokenManager,
    @Autowired private val cameraRepo: CameraRepo,
    @Autowired private val rabbitAdmin: RabbitAdmin,
    @Autowired private val messagingProperties: MessagingProperties,
    @Autowired private val streamingConsumer: StreamingConsumer,

) {
    private val mapper = jacksonObjectMapper()

    @BeforeEach
    @AfterEach
    fun cleanup() {
        rabbitAdmin.purgeQueue(messagingProperties.streamingCheckQueue, false)
        cameraRepo.deleteAllWithQuery()
    }

    @Test
    fun `list no token`() {
        mockMvc.get("/v1/live/camera").andExpect {
            status { is3xxRedirection() }
            header { }
            redirectedUrl("/sso/login")
        }
    }

    @Test
    fun `list empty`() {
        mockMvc.get("/v1/live/camera") {
            headers {
                setBearerAuth(token())
                accept = listOf(MediaType.APPLICATION_JSON)
            }
        }.andExpect {
            status { isOk() }
            content {
                json(
                    """{
  "success": true,
  "message": "ok",
  "data": []
}""",
                    strict = false
                )
            }
        }
    }

    @Test
    fun `create camera and get live view url`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            host = "127.0.0.1",
            httpPort = 80,
            rtspPort = 554,
            channel = 1,
            userName = "admin",
            password = "Password123",
            longitude = 2.0,
            latitude = 3.0,
            type = CameraType.HIKVISION,
        )
        mockMvc.post("/v1/camera") {
            content = mapper.writeValueAsString(camera)
            headers {
                setBearerAuth(token())
                contentType = MediaType.APPLICATION_JSON
                accept = listOf(MediaType.APPLICATION_JSON)
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
    "vmsCameraIndexCode": "00001",
    "vmsType": null,
    "name": "Test 01",
    "location": "01",
    "latitude": 3.0,
    "longitude": 2.0,
    "host": "127.0.0.1",
    "httpPort": 80,
    "rtspPort": 554,
    "channel": 1,
    "captureQualityChannel": "01",
    "userName": "admin",
    "password": "Password123",
    "isActive": true,
    "isStreetvendor": false,
    "isTraffic": false,
    "isCrowd": false,
    "isTrash": false,
    "isFlood": false,
    "type": "HIKVISION",
    "label": null
  }
}"""
                )
            }
        }

        streamingConsumer.doCheck()
        mockMvc.get("/v1/live/camera") {
            headers {
                setBearerAuth(token())
                accept = listOf(MediaType.APPLICATION_JSON)
            }
        }.andExpect {
            status { isOk() }
            content {
                json(
                    """{
  "success": true,
  "message": "ok",
  "data": [
    {
      "vmsCameraIndexCode": "00001",
      "vmsType": null,
      "name": "Test 01",
      "location": "01",
      "latitude": 3.0,
      "longitude": 2.0,
      "isActive": true,
      "isStreetvendor": false,
      "isTraffic": false,
      "isCrowd": false,
      "isTrash": false,
      "isFlood": false,
      "label": null
    }
  ]
}""",
                    strict = false
                )
                jsonPath("\$.data[0].liveViewUrl") {
                    isNotEmpty()
                    isString()
                }
            }
        }
    }
    @Test
    fun `live view parameters`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            isCrowd = true,
            isTraffic = false,
            interior = CameraInterior(
                isLiveView = null,
                lastCaptureMethod = null,
                isPing = null,
                pingResponseTimeSec = null,
                pingRawData = null,
                pingLast = null,
                liveViewHash = null,
                liveViewUrl = null,
            )
        )
        cameraRepo.saveAndFlush(camera)

        mockMvc.get("/v1/live/camera?keyword=none") {
            headers {
                setBearerAuth(token())
                accept = listOf(MediaType.APPLICATION_JSON)
            }
        }.andExpect {
            status { isOk() }
            content {
                json(
                    """{
  "success": true,
  "message": "ok",
  "data": []
}""",
                    strict = false
                )
            }
        }
        mockMvc.get("/v1/live/camera?location=none") {
            headers {
                setBearerAuth(token())
                accept = listOf(MediaType.APPLICATION_JSON)
            }
        }.andExpect {
            status { isOk() }
            content {
                json(
                    """{
  "success": true,
  "message": "ok",
  "data": []
}""",
                    strict = false
                )
            }
        }
        mockMvc.get("/v1/live/camera?type=TRAFFIC") {
            headers {
                setBearerAuth(token())
                accept = listOf(MediaType.APPLICATION_JSON)
            }
        }.andExpect {
            status { isOk() }
            content {
                json(
                    """{
  "success": true,
  "message": "ok",
  "data": []
}""",
                    strict = false
                )
            }
        }
        mockMvc.get("/v1/live/camera?id=${UUID.randomUUID()}") {
            headers {
                setBearerAuth(token())
                accept = listOf(MediaType.APPLICATION_JSON)
            }
        }.andExpect {
            status { isOk() }
            content {
                json(
                    """{
  "success": true,
  "message": "ok",
  "data": []
}""",
                    strict = false
                )
            }
        }
        mockMvc.get("/v1/live/location/list") {
            headers {
                setBearerAuth(token())
                accept = listOf(MediaType.APPLICATION_JSON)
            }
        }.andExpect {
            status { isOk() }
            content {
                json(
                    """{
  "success": true,
  "message": "ok",
  "data": [
  "01"
  ]
}""",
                    strict = false
                )
            }
        }
        mockMvc.get("/v1/live/location/list?keyword=none") {
            headers {
                setBearerAuth(token())
                accept = listOf(MediaType.APPLICATION_JSON)
            }
        }.andExpect {
            status { isOk() }
            content {
                json(
                    """{
  "success": true,
  "message": "ok",
  "data": []
}""",
                    strict = false
                )
            }
        }
    }

    @Test
    fun `create minimum camera and get live view url`() {
        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01",
            interior = CameraInterior(
                isLiveView = null,
                lastCaptureMethod = null,
                isPing = null,
                pingResponseTimeSec = null,
                pingRawData = null,
                pingLast = null,
                liveViewHash = null,
                liveViewUrl = null,
            )
        )
        cameraRepo.saveAndFlush(camera)

        mockMvc.get("/v1/live/camera") {
            headers {
                setBearerAuth(token())
                accept = listOf(MediaType.APPLICATION_JSON)
            }
        }.andExpect {
            status { isOk() }
            content {
                json(
                    """{
  "success": true,
  "message": "ok",
  "data": [
    {
      "vmsCameraIndexCode": "00001",
      "vmsType": null,
      "name": "Test 01",
      "location": "01",
      "latitude": 0.0,
      "longitude": 0.0,
      "isActive": true,
      "isStreetvendor": false,
      "isTraffic": false,
      "isCrowd": false,
      "isTrash": false,
      "isFlood": false,
      "label": null,
      "liveViewUrl": null
    }
  ]
}""",
                    strict = false
                )
            }
        }
    }

    private fun token(): String = tokenManager.accessToken("banyuwangi-test", "banyuwangi-test")
}
