package com.katalisindonesia.banyuwangi.consumer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BoundingBox(

    var corners: List<Corners> = listOf(),
    var width: Double? = null,
    var height: Double? = null

)
