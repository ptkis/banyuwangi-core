package com.katalisindonesia.banyuwangi

import com.katalisindonesia.banyuwangi.model.DetectionType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.net.URI
import java.time.temporal.ChronoUnit

@ConfigurationProperties(prefix = "dashboard.app")
@ConstructorBinding
data class AppProperties(
    val baseUri: URI,
    val timeoutSeconds: Long = 60,
    val captureErrorBackoffSeconds: Long,
    val captureDelaySeconds: Long,
    val defaultSize: Int,
    val alarmTopic: String,
    val snapshotCountZeroDelaySeconds: Long,
    /**
     * Minimum
     */
    val minFreeSpace: Long,
    val batchSize: Int,

    val totalTruncateChronoUnit: ChronoUnit,
    val totalPreferredProperty: TotalPreferredProperty,

    val chartCacheSeconds: Long,
    val detectionCacheSeconds: Long,

    val detectionMinConfidence: Double,
    val detectionMinConfidences: Map<DetectionType?, Double> = emptyMap(),
    val detectionFloodFloodValue: Int,

    val alarmTitles: Map<DetectionType, String>,
    val alarmMessages: Map<DetectionType, String>,
    val alarmHighMinimalValues: Map<DetectionType, Int>,
    val alarmHighTitles: Map<DetectionType, String>,
    val alarmHighMessages: Map<DetectionType, String>,

    val fcmRateLimit: Double,
)
