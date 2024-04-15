package com.katalisindonesia.banyuwangi.consumer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class DetectionRequest(
    val uuid: UUID,
    val imageUri: String,
    val callbackQueue: String,
    val dataset: Dataset? = null,
)
