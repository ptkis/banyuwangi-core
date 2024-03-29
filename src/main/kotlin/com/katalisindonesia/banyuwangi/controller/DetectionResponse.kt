package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.model.Annotation
import com.katalisindonesia.banyuwangi.model.DetectionType
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class DetectionResponse(
    val date: LocalDate,
    val instant: Instant,
    val location: String,
    val cameraName: String,
    val type: DetectionType,
    val value: Int,
    val imageSrc: String,
    val annotations: List<Annotation>,
    val longitude: BigDecimal,
    val latitude: BigDecimal,
)
