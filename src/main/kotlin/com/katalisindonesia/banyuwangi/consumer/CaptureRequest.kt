package com.katalisindonesia.banyuwangi.consumer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.CameraInterior
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class CaptureRequest(
    val camera: Camera,
    val cameraInterior: CameraInterior,
    val instant: Instant,
)
