package com.katalisindonesia.banyuwangi.consumer

data class BoundingBox(

    var corners: List<Corners> = listOf(),
    var width: Double? = null,
    var height: Double? = null

)
