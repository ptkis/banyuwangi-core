package com.katalisindonesia.banyuwangi.consumer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DetectionResponse(
    val success: Boolean = true,
    val message: String = "ok",
    val response: List<Detection>,
    val request: DetectionRequest,
)
