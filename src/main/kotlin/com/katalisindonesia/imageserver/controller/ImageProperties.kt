package com.katalisindonesia.imageserver.controller

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.util.UUID

@ConfigurationProperties(prefix = "image")
@ConstructorBinding

class ImageProperties (
    val folder: String = "images",
    val dummyId: UUID= UUID.randomUUID()
)
