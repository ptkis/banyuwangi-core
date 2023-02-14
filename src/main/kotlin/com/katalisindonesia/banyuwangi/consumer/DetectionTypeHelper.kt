package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.DetectionType
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

class DetectionTypeHelper(private val appProperties: AppProperties) {
    private val list: Map<DetectionType, Set<String>> = mapOf(
        DetectionType.CROWD to setOf("person"),
        DetectionType.TRAFFIC to setOf("bus", "car", "motorcycle", "truck"),
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

    fun deduce(label: String): Deduction? {
        val mapVal = map[label]
        if (mapVal != null) {
            return Deduction(
                type = mapVal,
                value = 1,
            )
        }

        val deduction = when {
            label.startsWith("garbage") -> {
                Deduction(type = DetectionType.TRASH, value = 1)
            }

            label.startsWith("flood-flood") -> {
                Deduction(type = DetectionType.FLOOD, value = appProperties.detectionFloodFloodValue)
            }
            label.startsWith("flood") -> {
                Deduction(type = DetectionType.FLOOD, value = 1)
            }

            label.startsWith("traffic") -> {
                Deduction(type = DetectionType.TRAFFIC, value = 1)
            }

            label.startsWith("streetvendor") -> {
                Deduction(type = DetectionType.STREETVENDOR, value = 1)
            }

            label.startsWith("crowd") -> {
                Deduction(type = DetectionType.CROWD, value = 1)
            }

            else -> null
        }

        return deduction
    }
}
