package com.katalisindonesia.banyuwangi

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.net.URI

@ConfigurationProperties(prefix = "dashboard.app")
@ConstructorBinding
data class AppProperties(
    val baseUri: URI,
    val timeoutSeconds: Long = 60,
)
