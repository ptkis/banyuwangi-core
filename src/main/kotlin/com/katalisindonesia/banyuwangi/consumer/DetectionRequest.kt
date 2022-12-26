package com.katalisindonesia.banyuwangi.consumer

import java.util.UUID

data class DetectionRequest(
    val uuid: UUID,
    val imageUri: String,
    val callbackQueue: String,
    val dataset: Dataset? = null,
)
