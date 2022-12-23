package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.model.SnapshotCount
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ChartHelper {
    internal fun chartData(counts: List<SnapshotCount>): ChartData<ZonedDateTime> {
        val seriesNames = mutableSetOf<String>()
        val data = mutableMapOf<String, MutableList<Long>>()
        val imageIds = mutableMapOf<String, MutableList<String>>()

        val map = mutableMapOf<ZonedDateTime, MutableMap<String, Pair<Long, String>>>()
        for (count in counts) {
            val zonedDateTime = count.snapshotCreated.truncatedTo(ChronoUnit.SECONDS).atZone(ZoneId.systemDefault())
            val inner = map.getOrPut(zonedDateTime) { mutableMapOf() }
            val cameraName = count.snapshotCameraName
            val value = inner.getOrPut(cameraName) { Pair(0L, count.snapshotImageId.toString()) }

            inner[cameraName] = Pair(value.first + count.value, count.snapshotImageId.toString())

            seriesNames.add(cameraName)
        }

        for (entry in map.entries) {
            val innerMap = entry.value
            for (seriesName in seriesNames) {
                data.getOrPut(seriesName) { mutableListOf() }.add(innerMap[seriesName]?.first ?: 0L)
                imageIds.getOrPut(seriesName) { mutableListOf() }.add(innerMap[seriesName]?.second ?: "")
            }
        }

        return ChartData(
            seriesNames = seriesNames.toList(),
            labels = map.keys.toList(),
            data = data,
            snapshotIds = imageIds,
        )
    }
}
