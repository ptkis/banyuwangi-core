package com.katalisindonesia.banyuwangi.consumer

data class DetectionResponse(
    val success: Boolean = true,
    val message: String = "ok",
    val response: List<Detection>,
    val request: DetectionRequest,
)
