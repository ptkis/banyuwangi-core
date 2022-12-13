package com.katalisindonesia.banyuwangi.service

import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.CameraType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(SpringExtension::class)
@SpringBootTest
class CaptureServiceTest(
    @Autowired
    private val captureService: CaptureService,
) {

/*
    @Test
    fun `onvif empty`() {
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
        val result = captureService.onvif(camera)
        assertTrue(result.isEmpty)
    }
*/

    @Test
    fun empty() = runTest {
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
        val result = captureService.empty(camera)
        assertTrue(result.isEmpty)
    }
}
