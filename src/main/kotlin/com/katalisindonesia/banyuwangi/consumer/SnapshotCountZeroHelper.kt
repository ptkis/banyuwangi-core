package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class SnapshotCountZeroHelper(private val timeFun: () -> Instant = { Instant.now() }) {
    private val lastZeroMap = ConcurrentHashMap<UUID, Instant>()

    fun removeZeros(cameraId: UUID, map: MutableMap<DetectionType, SnapshotCount>, delaySeconds: Long) {
        val lastZeroInstant = lastZeroMap[cameraId]
        if (lastZeroInstant != null && lastZeroInstant.plusSeconds(delaySeconds).isAfter(
                timeFun.invoke()
            )
        ) {
            // remove zero values
            val iterator = map.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next.value.value == 0) {
                    iterator.remove()
                }
            }
        } else {
            lastZeroMap[cameraId] = timeFun.invoke()
        }
    }
}
