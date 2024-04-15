package com.katalisindonesia.banyuwangi.consumer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.katalisindonesia.banyuwangi.model.DetectionType

@JsonIgnoreProperties(ignoreUnknown = true)
data class Deduction(
    val type: DetectionType,
    val value: Int,
)
