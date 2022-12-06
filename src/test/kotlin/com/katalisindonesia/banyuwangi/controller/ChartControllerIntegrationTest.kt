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
import java.net.InetSocketAddress

/**
 * Integration test for uploading to OBS.
 *
 * @author Thomas Edwin Santosa
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ChartControllerIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val tokenManager: TokenManager,

) {
    private val types = arrayOf("flood", "trash", "streetvendor", "crowd", "traffic")

    @Test
    fun `chart without token should redirect`() {
        for (type in types) {
            mockMvc.get("/v1/chart/$type").andExpect {
                status {
                    is3xxRedirection()
                }
            }
        }
    }

    private fun token(): String = tokenManager.accessToken("banyuwangi-test", "banyuwangi-test")

    @Test
    fun `chart with token`() {

        val token = token()

        for (type in types) {
            mockMvc.get("/v1/chart/$type") {
                headers {
                    setBearerAuth(token)
                    host = InetSocketAddress("localhost", 4200)
                }
            }.andExpect {
                status {
                    isOk()
                }
            }
        }
    }
}
