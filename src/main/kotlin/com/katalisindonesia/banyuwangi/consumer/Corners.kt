package com.katalisindonesia.banyuwangi.consumer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Corners(

    var x: Double? = null,
    var y: Double? = null

)
