package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.security.TokenManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/**
 * Integration test for uploading to OBS.
 *
 * @author Thomas Edwin Santosa
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BrowseControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val tokenManager: TokenManager,

    ) {

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
}
