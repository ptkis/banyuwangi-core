package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.repo.AlarmRepo
import com.katalisindonesia.banyuwangi.security.TokenManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AlarmControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val alarmRepo: AlarmRepo,
    @Autowired private val tokenManager: TokenManager,

) {

    @BeforeEach
    @AfterEach
    fun cleanup() {
        alarmRepo.deleteAll()
    }

    @Test
    fun `list no token`() {
        mockMvc.get("/v1/alarm/list").andExpect {
            status { is3xxRedirection() }
        }
    }

    @Test
    fun `list empty`() {
        mockMvc.get("/v1/alarm/list") {
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
                    strict = false,
                )
            }
        }
    }

    @Test
    fun `list and count empty with parameters`() {
        mockMvc.get(
            "/v1/alarm/list?beginning=2022-01-01" +
                "&ending=2022-01-02&detectionType=TRASH"
        ) {
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
                    strict = false,
                )
            }
        }
        mockMvc.get(
            "/v1/alarm/count?beginning=2022-01-01" +
                "&ending=2022-01-02&detectionType=TRASH"
        ) {
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
  "data": 0
}""",
                    strict = false,
                )
            }
        }
    }

    private fun token(): String = tokenManager.accessToken("banyuwangi-test", "banyuwangi-test")
}
