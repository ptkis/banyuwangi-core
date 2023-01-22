package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.CameraInterior
import com.katalisindonesia.banyuwangi.model.CameraType
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotRepo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("secret", "default")
class CaptureConsumerTest(
    @Autowired
    private val captureConsumer: CaptureConsumer,

    @Autowired
    private val cameraRepo: CameraRepo,

    @Autowired
    private val snapshotRepo: SnapshotRepo,

    @Value("\${test.hikvision.host}") private val host: String,
    @Value("\${test.hikvision.port}") private val port: Int,
    @Value("\${test.hikvision.user}") private val user: String,
    @Value("\${test.hikvision.password}") private val password: String,
) {

    @BeforeEach
    @AfterEach
    fun cleanup() {
        snapshotRepo.deleteAll()
        cameraRepo.deleteAll()
    }

    @Test
    fun capture() {

        var success = false
        for (channel in 1..16) {
            val camera = Camera(
                name = "Camera $channel",
                location = "Sawah",
                host = host,
                channel = channel,
                captureQualityChannel = "01",
                httpPort = port,
                userName = user,
                password = password,
                type = CameraType.HIKVISION,
            )
            cameraRepo.saveAndFlush(camera)
            if (captureConsumer.doOnCapture(
                    CaptureRequest(
                            camera = camera,
                            cameraInterior = camera.interior ?: CameraInterior(),
                            instant = Instant.now(),
                        )
                )
            ) {
                success = true

                val camera1 = cameraRepo.getReferenceById(camera.id)
                assertNotNull(camera1.interior?.lastCaptureInstant)
                assertNotNull(camera1.interior?.lastCaptureMethod)
                break
            }
        }
        assertTrue(success) { "Must find a camera" }
    }

    @Test
    fun `capture with error should backoff`() {

        val camera = Camera(
            name = "Camera 1",
            location = "Sawah",
            host = "250.250.250.250",
            channel = 1,
            captureQualityChannel = "01",
            httpPort = 80,
            userName = "test",
            password = "test",
            type = CameraType.HIKVISION,
        )
        cameraRepo.saveAndFlush(camera)

        val success = captureConsumer.doOnCapture(
            CaptureRequest(
                camera = camera,
                cameraInterior = camera.interior ?: CameraInterior(),
                instant = Instant.now(),
            )
        )
        Assertions.assertFalse(success)

        val camera1 = cameraRepo.getReferenceById(camera.id)
        val interior = camera1.interior!!
        val nextCaptureAfterErrorInstant = interior.nextCaptureAfterErrorInstant
        assertNotNull(nextCaptureAfterErrorInstant)
        assertNotEquals("", interior.lastCaptureErrorMessage)
        val lastCaptureErrorInstant = interior.lastCaptureErrorInstant
        assertNotNull(lastCaptureErrorInstant)
        assertTrue(Instant.now().isBefore(nextCaptureAfterErrorInstant!!))

        val success1 = captureConsumer.doOnCapture(
            CaptureRequest(
                camera = camera1,
                cameraInterior = camera1.interior ?: CameraInterior(),
                instant = Instant.now(),
            )
        )
        Assertions.assertFalse(success1)

        val camera2 = cameraRepo.getReferenceById(camera.id)
        val interior2 = camera2.interior!!
        val nextCaptureAfterErrorInstant2 = interior2.nextCaptureAfterErrorInstant
        assertNotNull(nextCaptureAfterErrorInstant2)
        assertNotEquals("", interior2.lastCaptureErrorMessage)
        assertEquals(nextCaptureAfterErrorInstant, nextCaptureAfterErrorInstant2)
    }
}
