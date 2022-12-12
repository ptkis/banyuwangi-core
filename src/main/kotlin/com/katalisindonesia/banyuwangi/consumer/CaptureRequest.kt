package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.Camera
import java.time.Instant

data class CaptureRequest(
    val camera: Camera,
    val instant: Instant,
)
