package com.katalisindonesia.banyuwangi.consumer

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "dashboard.messaging")
@ConstructorBinding
class MessagingProperties(
    val captureQueue: String,

    val detectionQueue: String,

    val detectionResultQueue: String,
    val streamingCheckQueue: String,
    val triggerQueue: String,
    val totalQueue: String,

    val captureTtl: Long,
    val detectionTtl: Long,
    val streamingCheckTtl: Long,
    val triggerTtl: Long,
    val totalTtl: Long,
)
