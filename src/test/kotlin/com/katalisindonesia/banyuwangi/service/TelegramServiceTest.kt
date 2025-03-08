package com.katalisindonesia.banyuwangi.service

import com.github.kotlintelegrambot.entities.ChatId
import com.katalisindonesia.banyuwangi.model.Alarm
import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.Snapshot
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import com.katalisindonesia.banyuwangi.repo.TelegramChatRepo
import com.katalisindonesia.imageserver.service.StorageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles("default", "secret")
class TelegramServiceTest(
    @Autowired
    private val telegramService: TelegramService,
    @Autowired
    private val telegramChatRepo: TelegramChatRepo,
    @Autowired
    private val storageService: StorageService,
) {
    @Test
    fun test_start_stop() {
        telegramService.start(ChatId.fromId(10))

        assertEquals(1, telegramChatRepo.findAll().size)

        // duplicate
        telegramService.start(ChatId.fromId(10))
        assertEquals(1, telegramChatRepo.findAll().size)

        // different
        telegramService.start(ChatId.fromId(59573981))
        assertEquals(2, telegramChatRepo.findAll().size)

        // stop
        telegramService.stop(ChatId.fromId(59573981))
        assertEquals(1, telegramChatRepo.findAll().size)

        // duplicate stop
        telegramService.stop(ChatId.fromId(59573981))
        assertEquals(1, telegramChatRepo.findAll().size)

        // all stop
        telegramService.stop(ChatId.fromId(10))
        assertEquals(0, telegramChatRepo.findAll().size)
    }

    @Test
    fun sendAlarm() {
        telegramService.start(ChatId.fromId(59573981))
        val camera0 = Camera(name = "camera0", location = "")

        val snapshot0 = Snapshot(imageId = imageId(), camera = camera0, length = 0, isAnnotation = true)

        val base = ZonedDateTime.of(1970, 1, 1, 7, 0, 0, 0, ZoneId.systemDefault()).toInstant()
        snapshot0.created = base.plusMillis(2100)

        val count0 = SnapshotCount(
            snapshot = snapshot0,
            type = DetectionType.CROWD,
            value = 2,
        )
        telegramService.sendAlarm(
            Alarm(
                maxValue = 1,
                snapshotCount = count0,
            )
        )
        telegramService.stop(ChatId.fromId(59573981))
    }
    @Test
    fun sendAlarmFloodHigh() {
        telegramService.start(ChatId.fromId(59573981))
        val camera0 = Camera(name = "camera0", location = "")

        val snapshot0 = Snapshot(imageId = imageId(), camera = camera0, length = 0, isAnnotation = true)

        val base = ZonedDateTime.of(1970, 1, 1, 7, 0, 0, 0, ZoneId.systemDefault()).toInstant()
        snapshot0.created = base.plusMillis(2100)

        val count0 = SnapshotCount(
            snapshot = snapshot0,
            type = DetectionType.FLOOD,
            value = 100,
        )
        telegramService.sendAlarm(
            Alarm(
                maxValue = 1,
                snapshotCount = count0,
            )
        )
        telegramService.stop(ChatId.fromId(59573981))
    }
    @Test
    fun sendAlarmFloodLow() {
        telegramService.start(ChatId.fromId(59573981))
        val camera0 = Camera(name = "camera0", location = "")

        val snapshot0 = Snapshot(imageId = imageId(), camera = camera0, length = 0, isAnnotation = true)

        val base = ZonedDateTime.of(1970, 1, 1, 7, 0, 0, 0, ZoneId.systemDefault()).toInstant()
        snapshot0.created = base.plusMillis(2100)

        val count0 = SnapshotCount(
            snapshot = snapshot0,
            type = DetectionType.FLOOD,
            value = 1,
        )
        telegramService.sendAlarm(
            Alarm(
                maxValue = 1,
                snapshotCount = count0,
            )
        )
        telegramService.stop(ChatId.fromId(59573981))
    }

    private fun imageId(): UUID {
        val imageId = storageService.store(ClassPathResource("dog_bike_car.jpg").inputStream.readAllBytes())
        return imageId
    }
}
