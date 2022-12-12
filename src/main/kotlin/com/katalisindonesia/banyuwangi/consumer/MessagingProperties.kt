package com.katalisindonesia.banyuwangi.consumer

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.util.UUID

@ConfigurationProperties(prefix = "dashboard.messaging")
@ConstructorBinding
class MessagingProperties(
    val captureQueue: String = "/dashboard/v1/capture" + UUID.randomUUID(),

    val detectionQueue: String = "/dashboard/v1/detection" + UUID.randomUUID(),

    val detectionResultQueue: String = "/dashboard/v1/detectionResult" + UUID.randomUUID(),

    val defaultQueueTtl: Long = 30000
)
