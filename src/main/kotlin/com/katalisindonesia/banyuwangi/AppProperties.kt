package com.katalisindonesia.banyuwangi

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
)
