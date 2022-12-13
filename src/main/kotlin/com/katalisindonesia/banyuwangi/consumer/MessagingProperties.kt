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

    val defaultQueueTtl: Long = 30000
)
