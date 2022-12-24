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
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

@SpringBootTest
@ExtendWith(SpringExtension::class)
class AlarmServiceTest(
    @Autowired
    private val alarmService: AlarmService,
) {

    @Test
    fun sendAlarm() {
        val registrationToken =
            "fvGlRsqfxoTcuIYOevTdOg:APA91bGXWNKM_lq0GWmA9GMJ_SL9rLcw6C-DXTEM5g_aa4nixi38QArg0JjWe1UXb" +
                "NlKqKpm06cg_-G6ZWptQAcDzUAvgu8qjDEhtc50UpND0n2JTL0ZsUw2rAphh3P9b-gj5uHndDg_"
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
}