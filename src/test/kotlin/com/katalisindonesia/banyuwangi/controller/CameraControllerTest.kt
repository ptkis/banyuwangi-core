package com.katalisindonesia.banyuwangi.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import com.katalisindonesia.banyuwangi.security.TokenManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("secret", "default")
class CameraControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val tokenManager: TokenManager,
    @Autowired private val cameraRepo: CameraRepo,

) {

    private val mapper = jacksonObjectMapper()

    @BeforeEach
    @AfterEach
    fun cleanup() {
        cameraRepo.deleteAll()
    }

    @Test
    fun `list no token`() {
        mockMvc.get("/v1/camera").andExpect {
            status { is3xxRedirection() }
            header { }
            redirectedUrl("/sso/login")
        }
    }

    @Test
    fun `list empty`() {
        mockMvc.get("/v1/camera") {
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
  "data": {
    "content": [],
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
    "totalPages": 0,
    "totalElements": 0,
    "first": true,
    "size": 1000,
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "numberOfElements": 0,
    "empty": true
  }
}""",
                    strict = false
                )
            }
        }
    }

    @Test
    fun `create list edit list`() {
        // DOUBLE CREATE, should be idempotent

        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01"
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
    "latitude": 0.0,
    "longitude": 0.0,
    "host": "",
    "httpPort": 80,
    "rtspPort": 554,
    "channel": 1,
    "captureQualityChannel": "01",
    "userName": "",
    "password": "",
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
    "latitude": 0.0,
    "longitude": 0.0,
    "host": "",
    "httpPort": 80,
    "rtspPort": 554,
    "channel": 1,
    "captureQualityChannel": "01",
    "userName": "",
    "password": "",
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
        camera.version = 0L

        // LIST

        mockMvc.get("/v1/camera") {
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
  "data": {
    "content": [
      {
        "vmsCameraIndexCode": "00001",
        "vmsType": null,
        "name": "Test 01",
        "location": "01",
        "latitude": 0.0,
        "longitude": 0.0,
        "host": "",
        "httpPort": 80,
        "rtspPort": 554,
        "channel": 1,
        "captureQualityChannel": "01",
        "userName": "",
        "password": "",
        "isActive": true,
        "isStreetvendor": false,
        "isTraffic": false,
        "isCrowd": false,
        "isTrash": false,
        "isFlood": false,
        "type": "HIKVISION",
        "label": null
      }
    ],
    "pageable": {
      "sort": {
        "empty": false,
        "unsorted": false,
        "sorted": true
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 1000,
      "paged": true,
      "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 1,
    "first": true,
    "size": 1000,
    "number": 0,
    "sort": {
      "empty": false,
      "unsorted": false,
      "sorted": true
    },
    "numberOfElements": 1,
    "empty": false
  }
}""",
                    strict = false
                )
            }
        }

        camera.name = "Test 02"
        camera.vmsCameraIndexCode = "0002"

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
                camera.version = (camera.version ?: 0L) + 1L
                json(
                    mapper.writeValueAsString(
                        WebResponse(
                            success = true, message = "ok",
                            data = camera
                        )
                    )
                )
            }
        }
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
                camera.version = (camera.version ?: 0L) + 1L
                json(
                    mapper.writeValueAsString(
                        WebResponse(
                            success = true, message = "ok",
                            data = camera
                        )
                    )
                )
            }
        }

        // LIST

        mockMvc.get("/v1/camera") {
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
  "data": {
    "content": [
      {
        "vmsCameraIndexCode": "0002",
        "vmsType": null,
        "name": "Test 02",
        "location": "01",
        "latitude": 0.0,
        "longitude": 0.0,
        "host": "",
        "httpPort": 80,
        "rtspPort": 554,
        "channel": 1,
        "captureQualityChannel": "01",
        "userName": "",
        "password": "",
        "isActive": true,
        "isStreetvendor": false,
        "isTraffic": false,
        "isCrowd": false,
        "isTrash": false,
        "isFlood": false,
        "type": "HIKVISION",
        "label": null
      }
    ],
    "pageable": {
      "sort": {
        "empty": false,
        "unsorted": false,
        "sorted": true
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 1000,
      "paged": true,
      "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 1,
    "first": true,
    "size": 1000,
    "number": 0,
    "sort": {
      "empty": false,
      "unsorted": false,
      "sorted": true
    },
    "numberOfElements": 1,
    "empty": false
  }
}""",
                    strict = false
                )
            }
        }
    }

    @Test
    fun `bulk delete list`() {
        // CREATE

        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01"
        )
        mockMvc.post("/v1/camera/bulk") {
            content = mapper.writeValueAsString(listOf(camera))
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
  "data": [{
    "vmsCameraIndexCode": "00001",
    "vmsType": null,
    "name": "Test 01",
    "location": "01",
    "latitude": 0.0,
    "longitude": 0.0,
    "host": "",
    "httpPort": 80,
    "rtspPort": 554,
    "channel": 1,
    "captureQualityChannel": "01",
    "userName": "",
    "password": "",
    "isActive": true,
    "isStreetvendor": false,
    "isTraffic": false,
    "isCrowd": false,
    "isTrash": false,
    "isFlood": false,
    "type": "HIKVISION",
    "label": null
  }
]}"""
                )
            }
        }

        // DELETE twice
        mockMvc.post("/v1/camera/bulk/delete") {
            content = mapper.writeValueAsString(listOf(camera.id))
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
  "data": [
    "${camera.id}"
  ]}"""
                )
            }
        }

        mockMvc.post("/v1/camera/bulk/delete") {
            content = mapper.writeValueAsString(listOf(camera.id))
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
  "data": [
    "${camera.id}"
  ]}"""
                )
            }
        }

        // LIST

        mockMvc.get("/v1/camera") {
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
  "data": {
    "content": [],
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
    "totalPages": 0,
    "totalElements": 0,
    "first": true,
    "size": 1000,
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "numberOfElements": 0,
    "empty": true
  }
}""",
                    strict = false
                )
            }
        }
    }

    @Test
    fun `bulk create list bulk edit list`() {
        // DOUBLE CREATE, should be idempotent

        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01"
        )
        mockMvc.post("/v1/camera/bulk") {
            content = mapper.writeValueAsString(listOf(camera))
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
  "data": [{
    "vmsCameraIndexCode": "00001",
    "vmsType": null,
    "name": "Test 01",
    "location": "01",
    "latitude": 0.0,
    "longitude": 0.0,
    "host": "",
    "httpPort": 80,
    "rtspPort": 554,
    "channel": 1,
    "captureQualityChannel": "01",
    "userName": "",
    "password": "",
    "isActive": true,
    "isStreetvendor": false,
    "isTraffic": false,
    "isCrowd": false,
    "isTrash": false,
    "isFlood": false,
    "type": "HIKVISION",
    "label": null
  }
]}"""
                )
            }
        }
        mockMvc.post("/v1/camera/bulk") {
            content = mapper.writeValueAsString(listOf(camera))
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
  "data": [{
    "vmsCameraIndexCode": "00001",
    "vmsType": null,
    "name": "Test 01",
    "location": "01",
    "latitude": 0.0,
    "longitude": 0.0,
    "host": "",
    "httpPort": 80,
    "rtspPort": 554,
    "channel": 1,
    "captureQualityChannel": "01",
    "userName": "",
    "password": "",
    "isActive": true,
    "isStreetvendor": false,
    "isTraffic": false,
    "isCrowd": false,
    "isTrash": false,
    "isFlood": false,
    "type": "HIKVISION",
    "label": null
  }
]}"""
                )
            }
        }
        camera.version = 0L

        // LIST

        mockMvc.get("/v1/camera") {
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
  "data": {
    "content": [
      {
        "vmsCameraIndexCode": "00001",
        "vmsType": null,
        "name": "Test 01",
        "location": "01",
        "latitude": 0.0,
        "longitude": 0.0,
        "host": "",
        "httpPort": 80,
        "rtspPort": 554,
        "channel": 1,
        "captureQualityChannel": "01",
        "userName": "",
        "password": "",
        "isActive": true,
        "isStreetvendor": false,
        "isTraffic": false,
        "isCrowd": false,
        "isTrash": false,
        "isFlood": false,
        "type": "HIKVISION",
        "label": null
      }
    ],
    "pageable": {
      "sort": {
        "empty": false,
        "unsorted": false,
        "sorted": true
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 1000,
      "paged": true,
      "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 1,
    "first": true,
    "size": 1000,
    "number": 0,
    "sort": {
      "empty": false,
      "unsorted": false,
      "sorted": true
    },
    "numberOfElements": 1,
    "empty": false
  }
}""",
                    strict = false
                )
            }
        }

        camera.name = "Test 02"
        camera.vmsCameraIndexCode = "0002"

        mockMvc.post("/v1/camera/bulk") {
            content = mapper.writeValueAsString(listOf(camera))
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
                camera.version = (camera.version ?: 0L) + 1L
                json(
                    mapper.writeValueAsString(
                        WebResponse(
                            success = true, message = "ok",
                            data = listOf(camera)
                        )
                    )
                )
            }
        }
        mockMvc.post("/v1/camera/bulk") {
            content = mapper.writeValueAsString(listOf(camera))
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
                camera.version = (camera.version ?: 0L) + 1L
                json(
                    mapper.writeValueAsString(
                        WebResponse(
                            success = true, message = "ok",
                            data = listOf(camera)
                        )
                    )
                )
            }
        }

        // LIST

        mockMvc.get("/v1/camera") {
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
  "data": {
    "content": [
      {
        "vmsCameraIndexCode": "0002",
        "vmsType": null,
        "name": "Test 02",
        "location": "01",
        "latitude": 0.0,
        "longitude": 0.0,
        "host": "",
        "httpPort": 80,
        "rtspPort": 554,
        "channel": 1,
        "captureQualityChannel": "01",
        "userName": "",
        "password": "",
        "isActive": true,
        "isStreetvendor": false,
        "isTraffic": false,
        "isCrowd": false,
        "isTrash": false,
        "isFlood": false,
        "type": "HIKVISION",
        "label": null
      }
    ],
    "pageable": {
      "sort": {
        "empty": false,
        "unsorted": false,
        "sorted": true
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 1000,
      "paged": true,
      "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 1,
    "first": true,
    "size": 1000,
    "number": 0,
    "sort": {
      "empty": false,
      "unsorted": false,
      "sorted": true
    },
    "numberOfElements": 1,
    "empty": false
  }
}""",
                    strict = false
                )
            }
        }
    }

    @Test
    fun `create duplicate`() {
        // CREATE

        mockMvc.post("/v1/camera") {
            content = mapper.writeValueAsString(
                Camera(
                    vmsCameraIndexCode = "00001",
                    name = "Test 01",
                    location = "01"
                )
            )
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
    "latitude": 0.0,
    "longitude": 0.0,
    "host": "",
    "httpPort": 80,
    "rtspPort": 554,
    "channel": 1,
    "captureQualityChannel": "01",
    "userName": "",
    "password": "",
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

        // CREATE 2

        mockMvc.post("/v1/camera") {
            content = mapper.writeValueAsString(
                Camera(
                    vmsCameraIndexCode = "00001",
                    name = "Test 01",
                    location = "01"
                )
            )
            headers {
                setBearerAuth(token())
                contentType = MediaType.APPLICATION_JSON
                accept = listOf(MediaType.APPLICATION_JSON)
            }
        }.andExpect {
            status {
                is4xxClientError()
            }
            content {
                json("""{"success":false,"data":null}""")
            }
        }
    }

    @Test
    fun loadById() {
        // CREATE

        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01"
        )
        mockMvc.post("/v1/camera") {
            content = mapper.writeValueAsString(
                camera
            )
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
    "latitude": 0.0,
    "longitude": 0.0,
    "host": "",
    "httpPort": 80,
    "rtspPort": 554,
    "channel": 1,
    "captureQualityChannel": "01",
    "userName": "",
    "password": "",
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

        // GET

        mockMvc.get("/v1/camera/id/{id}", camera.id) {
            headers {
                setBearerAuth(token())
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
    "latitude": 0.0,
    "longitude": 0.0,
    "host": "",
    "httpPort": 80,
    "rtspPort": 554,
    "channel": 1,
    "captureQualityChannel": "01",
    "userName": "",
    "password": "",
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

        // GET SHOULD BE EMPTY

        mockMvc.get("/v1/camera/id/{id}", UUID.randomUUID().toString()) {
            headers {
                setBearerAuth(token())
                accept = listOf(MediaType.APPLICATION_JSON)
            }
        }.andExpect {
            status {
                isNotFound()
            }
            content {
                json("""{"success":false,"message":"not found","data": null}""")
            }
        }
    }

    @Test
    fun loadByVmsCameraIndexCode() {
        // CREATE

        val camera = Camera(
            vmsCameraIndexCode = "00001",
            name = "Test 01",
            location = "01"
        )
        mockMvc.post("/v1/camera") {
            content = mapper.writeValueAsString(
                camera
            )
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
    "latitude": 0.0,
    "longitude": 0.0,
    "host": "",
    "httpPort": 80,
    "rtspPort": 554,
    "channel": 1,
    "captureQualityChannel": "01",
    "userName": "",
    "password": "",
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

        // GET

        mockMvc.get("/v1/camera/cameraIndexCode/{id}", "00001") {
            headers {
                setBearerAuth(token())
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
    "latitude": 0.0,
    "longitude": 0.0,
    "host": "",
    "httpPort": 80,
    "rtspPort": 554,
    "channel": 1,
    "captureQualityChannel": "01",
    "userName": "",
    "password": "",
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

        // GET SHOULD BE EMPTY

        mockMvc.get("/v1/camera/cameraIndexCode/{id}", "00002") {
            headers {
                setBearerAuth(token())
                accept = listOf(MediaType.APPLICATION_JSON)
            }
        }.andExpect {
            status {
                isNotFound()
            }
            content {
                json("""{"success":false,"message":"not found","data": null}""")
            }
        }
    }

    private fun token(): String = tokenManager.accessToken("banyuwangi-test", "banyuwangi-test")
}
