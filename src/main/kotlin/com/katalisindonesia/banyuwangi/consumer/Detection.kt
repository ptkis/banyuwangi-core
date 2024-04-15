package com.katalisindonesia.banyuwangi.consumer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Detection(
    var boundingBox: BoundingBox? = BoundingBox(),
    var className: String? = null,
    var probability: Double? = null
)
