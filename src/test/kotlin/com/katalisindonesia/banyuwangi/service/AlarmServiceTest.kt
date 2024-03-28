package com.katalisindonesia.banyuwangi.service

import com.katalisindonesia.banyuwangi.model.Alarm
import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.FcmToken
import com.katalisindonesia.banyuwangi.model.Snapshot
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles("default", "secret")
class AlarmServiceTest(
    @Autowired
    private val alarmService: AlarmService,
) {

    private val registrationToken = "fvGlRsqfxoTcuIYOevTdOg:APA91bH38to1aR0OkBDVT4E2eI" +
        "J5rAjLp_MSW303U7LV8d9XKO1kr8RsqwZIW0UpR_xzwWaUSYFoZqKycr" +
        "zB2_jIwrmB_fFR0l_QGFiLPPRC-_mrVdirhioBu7_pwdxbfInbh0VqOYk5"

    @Test
    fun sendAlarm() {
        alarmService.subscribe(
            listOf(
                FcmToken(
                    username = "test1",
                    registrationToken = registrationToken
                )
            )
        )
        val camera0 = Camera(name = "camera0", location = "")

        val snapshot0 = Snapshot(imageId = UUID.randomUUID(), camera = camera0, length = 0, isAnnotation = true)

        val base = ZonedDateTime.of(1970, 1, 1, 7, 0, 0, 0, ZoneId.systemDefault()).toInstant()
        snapshot0.created = base.plusMillis(2100)

        val count0 = SnapshotCount(
            snapshot = snapshot0,
            type = DetectionType.CROWD,
            value = 2,
        )
        alarmService.sendAlarm(
            Alarm(
                maxValue = 1,
                snapshotCount = count0,
            )
        )
        alarmService.unsubscribe(
            listOf(
                FcmToken(
                    username = "test1",
                    registrationToken = registrationToken
                )
            )
        )
    }
    @Test
    fun sendAlarmFloodHigh() {
        alarmService.subscribe(
            listOf(
                FcmToken(
                    username = "test1",
                    registrationToken = registrationToken
                )
            )
        )
        val camera0 = Camera(name = "camera0", location = "")

        val snapshot0 = Snapshot(imageId = UUID.randomUUID(), camera = camera0, length = 0, isAnnotation = true)

        val base = ZonedDateTime.of(1970, 1, 1, 7, 0, 0, 0, ZoneId.systemDefault()).toInstant()
        snapshot0.created = base.plusMillis(2100)

        val count0 = SnapshotCount(
            snapshot = snapshot0,
            type = DetectionType.FLOOD,
            value = 100,
        )
        alarmService.sendAlarm(
            Alarm(
                maxValue = 1,
                snapshotCount = count0,
            )
        )
        alarmService.unsubscribe(
            listOf(
                FcmToken(
                    username = "test1",
                    registrationToken = registrationToken
                )
            )
        )
    }
    @Test
    fun sendAlarmFloodLow() {
        alarmService.subscribe(
            listOf(
                FcmToken(
                    username = "test1",
                    registrationToken = registrationToken
                )
            )
        )
        val camera0 = Camera(name = "camera0", location = "")

        val snapshot0 = Snapshot(imageId = UUID.randomUUID(), camera = camera0, length = 0, isAnnotation = true)

        val base = ZonedDateTime.of(1970, 1, 1, 7, 0, 0, 0, ZoneId.systemDefault()).toInstant()
        snapshot0.created = base.plusMillis(2100)

        val count0 = SnapshotCount(
            snapshot = snapshot0,
            type = DetectionType.FLOOD,
            value = 1,
        )
        alarmService.sendAlarm(
            Alarm(
                maxValue = 1,
                snapshotCount = count0,
            )
        )
        alarmService.unsubscribe(
            listOf(
                FcmToken(
                    username = "test1",
                    registrationToken = registrationToken
                )
            )
        )
    }
}
