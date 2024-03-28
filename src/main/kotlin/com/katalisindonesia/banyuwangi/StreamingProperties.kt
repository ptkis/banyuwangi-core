package com.katalisindonesia.banyuwangi

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "streaming")
@ConstructorBinding
data class StreamingProperties(
    val server: String,
    val baseUrl: String,
    val streamingToken: String,
)
