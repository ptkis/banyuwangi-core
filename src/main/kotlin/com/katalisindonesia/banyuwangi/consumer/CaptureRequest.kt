package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.CameraInterior
import java.time.Instant

data class CaptureRequest(
    val camera: Camera,
    val cameraInterior: CameraInterior,
    val instant: Instant,
)
