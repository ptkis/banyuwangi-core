package com.katalisindonesia.imageserver.controller

import com.katalisindonesia.banyuwangi.BanyuwangiCoreApplication
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [BanyuwangiCoreApplication::class])
@AutoConfigureMockMvc
class ImageControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val imageProperties: ImageProperties,

    ) {

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
}
