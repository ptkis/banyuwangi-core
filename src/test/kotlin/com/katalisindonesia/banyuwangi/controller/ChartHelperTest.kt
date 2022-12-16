package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.Snapshot
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

class ChartHelperTest {
    @Test
    fun `chartData from snapshotCount`() {
        val camera0 = Camera(name = "camera0", location = "")
        val camera1 = Camera(name = "camera1", location = "")

        val snapshot0 = Snapshot(imageId = UUID.randomUUID(), camera = camera0, length = 0, isAnnotation = true)
        val snapshot1 = Snapshot(imageId = UUID.randomUUID(), camera = camera0, length = 0, isAnnotation = true)
        val snapshot2 = Snapshot(imageId = UUID.randomUUID(), camera = camera0, length = 0, isAnnotation = true)
        val snapshot3 = Snapshot(imageId = UUID.randomUUID(), camera = camera1, length = 0, isAnnotation = true)

        val base = ZonedDateTime.of(1970, 1, 1, 7, 0, 0, 0, ZoneId.systemDefault()).toInstant()
        snapshot0.created = base.plusMillis(2100)
        snapshot1.created = base.plusMillis(4100)
        snapshot2.created = base.plusMillis(6100)
        snapshot3.created = base.plusMillis(8100)

        val count0 = SnapshotCount(
            snapshot = snapshot0,
            type = DetectionType.CROWD,
            value = 1,
        )
        val count1 = SnapshotCount(
            snapshot = snapshot1,
            type = DetectionType.CROWD,
            value = 2,
        )
        val count2 = SnapshotCount(
            snapshot = snapshot2,
            type = DetectionType.CROWD,
            value = 3,
        )
        val count3 = SnapshotCount(
            snapshot = snapshot3,
            type = DetectionType.CROWD,
            value = 4,
        )
        val chartHelper = ChartHelper()
        val cd = chartHelper.chartData(listOf(count0, count1, count2, count3))

        println(cd)
        assertEquals(
            ChartData<ZonedDateTime>(
                seriesNames = listOf("camera0", "camera1"),
                labels = listOf(
                    ZonedDateTime.of(1970, 1, 1, 7, 0, 2, 0, ZoneId.systemDefault()),
                    ZonedDateTime.of(1970, 1, 1, 7, 0, 4, 0, ZoneId.systemDefault()),
                    ZonedDateTime.of(1970, 1, 1, 7, 0, 6, 0, ZoneId.systemDefault()),
                    ZonedDateTime.of(1970, 1, 1, 7, 0, 8, 0, ZoneId.systemDefault()),
                ),
                data = mapOf(
                    "camera0" to listOf(1L, 2L, 3L, 0),
                    "camera1" to listOf(0L, 0L, 0L, 4L),
                )
            ),
            cd
        )
    }
}
