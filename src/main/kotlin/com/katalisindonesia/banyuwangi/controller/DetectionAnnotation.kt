package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.model.BoundingBox

data class DetectionAnnotation(
    val boundingBox: BoundingBox,
    val confidence: Double,
)
