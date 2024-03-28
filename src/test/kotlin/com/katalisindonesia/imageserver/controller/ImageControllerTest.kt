package com.katalisindonesia.imageserver.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.katalisindonesia.banyuwangi.BanyuwangiCoreApplication
import com.katalisindonesia.banyuwangi.security.TokenManager
import com.katalisindonesia.imageserver.service.ProxyRequest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.io.File
import java.net.URI
import java.util.UUID

/**
 * Integration test for uploading to OBS.
 *
 * @author Thomas Edwin Santosa
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [BanyuwangiCoreApplication::class]
)
@AutoConfigureMockMvc
@ActiveProfiles("default", "secret")
@Disabled("Disable token base tests until foreign traffic to user's keycloak is unblocked")
class ImageControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val imageProperties: ImageProperties,
    @Autowired private val tokenManager: TokenManager,
) {
    private val mapper = jacksonObjectMapper()
    @Test
    fun `dummy image without token should success`() {
        mockMvc.get("/v1/image/{id}", imageProperties.dummyId).andExpect {
            status {
                isOk()
                content {
                    contentType(MediaType.IMAGE_JPEG)
                    bytes(ClassPathResource("/dog_bike_car.jpg").file.readBytes())
                }
            }
        }
    }

    @Test
    fun `random image without token should 404`() {
        mockMvc.get("/v1/image/{id}", UUID.randomUUID()).andExpect {
            status {
                isNotFound()
            }
        }
    }

    @Test
    fun `proxy image without token`() {
        mockMvc.post(
            "/v1/image/proxy",
            ProxyRequest(
                uri = URI("https://picsum.photos/200/300"),
                headers = emptyMap(),
            )
        ).andExpect {
            status {
                is3xxRedirection()
            }
        }
    }

    @Test
    fun `proxy image`() {
        val content = mockMvc.post(
            "/v1/image/proxy",
        ) {
            content = mapper.writeValueAsString(
                ProxyRequest(
                    uri = URI("https://picsum.photos/200/300"),
                    headers = emptyMap(),
                )
            )

            headers {
                setBearerAuth(token())
                accept = listOf(MediaType.IMAGE_JPEG)
                contentType = MediaType.APPLICATION_JSON
            }
        }
            .andExpect {
                status {
                    isOk()
                }
                content {
                    contentType(MediaType.IMAGE_JPEG)
                }
            }.andReturn().response.contentAsByteArray

        val folder = File("build/temp/ImageControllerTest")
        folder.mkdirs()

        val file = File(folder, "picsum${System.currentTimeMillis()}.jpg")
        file.writeBytes(content)
    }

    private fun token(): String = tokenManager.accessToken("banyuwangi-test", "banyuwangi-test")
}
