package com.katalisindonesia.banyuwangi.task

import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.CameraType
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles("secret")
class CaptureTaskTest(
    @Autowired
    private val captureTask: CaptureTask,
    @Autowired
    private val cameraRepo: CameraRepo,
) {
    @BeforeEach
    @AfterEach
    fun cleanup() {
        cameraRepo.deleteAll()
    }

    @Test
    fun testDoCapture() {
        val camera = Camera(
            name = "Camera 1",
            location = "Sawah",
            host = "127.0.0.1",
            channel = 1,
            captureQualityChannel = "01",
            httpPort = 80,
            userName = "user",
            password = "password",
            type = CameraType.HIKVISION,
        )
        cameraRepo.saveAndFlush(camera)

        val count = captureTask.doCapture()

        assertEquals(1, count)
    }
}
