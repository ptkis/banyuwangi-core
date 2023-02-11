package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.DetectionType
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

class DetectionTypeHelper {
    private val list: Map<DetectionType, Set<String>> = mapOf(
        DetectionType.CROWD to setOf("person"),
        DetectionType.TRAFFIC to setOf("bus", "car", "motorcycle", "truck"),
        DetectionType.STREETVENDOR to setOf("streetvendor"),
        DetectionType.FLOOD to setOf(
            "flood-puddle",
            "flood-flood",
        ),
        DetectionType.TRASH to setOf(
            "garbage-biodegradable",
            "garbage-cardboard",
            "garbage-glass",
            "garbage-metal",
            "garbage-paper",
            "garbage-plastic",
            "garbage-mixed",
        ),
    )

    private val map: Map<String, DetectionType> = calc()

    private fun calc(): Map<String, DetectionType> {
        val res = mutableMapOf<String, DetectionType>()
        for (entry in list) {
            for (str in entry.value) {
                res[str] = entry.key
            }
        }

        return Collections.unmodifiableMap(ConcurrentHashMap(res))
    }

    fun deduce(label: String): DetectionType? {
        val mapVal = map[label]
        if (mapVal != null) {
            return mapVal
        }

        val type = when {
            label.startsWith("garbage") -> {
                DetectionType.TRASH
            }

            label.startsWith("flood") -> {
                DetectionType.FLOOD
            }

            label.startsWith("traffic") -> {
                DetectionType.TRAFFIC
            }

            label.startsWith("streetvendor") -> {
                DetectionType.STREETVENDOR
            }

            label.startsWith("crowd") -> {
                DetectionType.CROWD
            }

            else -> null
        }

        return type
    }
}
