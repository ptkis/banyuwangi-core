package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.CameraType
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.AfterEach
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
@ActiveProfiles("secret")
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun capture() = runTest {

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
            withContext(Dispatchers.IO) {
                cameraRepo.saveAndFlush(camera)
            }
            if (captureConsumer.doOnCapture(
                    CaptureRequest(
                            camera = camera,
                            instant = Instant.now()
                        )
                )
            ) {
                success = true
                break
            }
        }
        assertTrue(success) { "Must find a camera" }
    }
}
