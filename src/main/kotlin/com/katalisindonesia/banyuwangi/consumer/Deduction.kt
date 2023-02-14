package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.DetectionType

data class Deduction(
    val type: DetectionType,
    val value: Int,
)
