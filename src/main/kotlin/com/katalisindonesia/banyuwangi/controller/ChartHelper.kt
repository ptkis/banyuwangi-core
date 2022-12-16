package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.model.SnapshotCount
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ChartHelper {
    internal fun chartData(counts: List<SnapshotCount>): ChartData<ZonedDateTime> {
        val seriesNames = mutableSetOf<String>()
        val data = mutableMapOf<String, MutableList<Long>>()

        val map = mutableMapOf<ZonedDateTime, MutableMap<String, Long>>()
        for (count in counts) {
            val zonedDateTime = count.snapshotCreated.truncatedTo(ChronoUnit.SECONDS).atZone(ZoneId.systemDefault())
            val inner = map.getOrPut(zonedDateTime) { mutableMapOf() }
            val cameraName = count.snapshotCameraName
            val value = inner.getOrPut(cameraName) { 0L }

            inner[cameraName] = value + count.value

            seriesNames.add(cameraName)
        }

        for (entry in map.entries) {
            val innerMap = entry.value
            for (seriesName in seriesNames) {
                data.getOrPut(seriesName) { mutableListOf() }.add(innerMap[seriesName] ?: 0L)
            }
        }

        return ChartData(seriesNames = seriesNames.toList(), labels = map.keys.toList(), data = data)
    }
}
