package com.katalisindonesia.banyuwangi.consumer

data class DetectionRequest(
    val imageUri: String,
    val callbackQueue: String,
)
