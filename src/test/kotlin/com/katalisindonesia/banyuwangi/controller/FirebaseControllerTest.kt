package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.repo.FcmTokenRepo
import com.katalisindonesia.banyuwangi.security.TokenManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.put

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class FirebaseControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val tokenManager: TokenManager,
    @Autowired private val fcmTokenRepo: FcmTokenRepo,

) {

    @BeforeEach
    @AfterEach
    fun cleanup() {
        fcmTokenRepo.deleteAll()
    }

    @Test
    fun `subscribe and unsubscribe without login`() {
        mockMvc.put("/v1/fcm/device/token/test1").andExpect {
            status { is3xxRedirection() }
            header { }
            redirectedUrl("/sso/login")
        }
        mockMvc.delete("/v1/fcm/device/token/test1").andExpect {
            status { is3xxRedirection() }
            header { }
            redirectedUrl("/sso/login")
        }
    }
    @Test
    fun `subscribe and unsubscribe with login`() {
        mockMvc.put("/v1/fcm/device/token/test1") {
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
    "registrationToken": "test1"
  }
}""",
                    strict = false
                )
            }
        }
        mockMvc.delete("/v1/fcm/device/token/test1") {
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
  "data": 1
}""",
                    strict = false
                )
            }
        }
    }

    private fun token(): String = tokenManager.accessToken("banyuwangi-test", "banyuwangi-test")
}
