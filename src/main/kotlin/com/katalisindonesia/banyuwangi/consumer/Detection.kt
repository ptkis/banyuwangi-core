package com.katalisindonesia.banyuwangi.consumer

data class Detection(
    var boundingBox: BoundingBox? = BoundingBox(),
    var className: String? = null,
    var probability: Double? = null
)
