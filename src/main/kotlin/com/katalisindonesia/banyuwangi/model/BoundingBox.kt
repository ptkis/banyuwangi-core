package com.katalisindonesia.banyuwangi.model

import javax.persistence.Column
import javax.persistence.Entity

@Entity
data class BoundingBox(

    @Column(nullable = false)
    var x: Double,

    @Column(nullable = false)
    var y: Double,

    @Column(nullable = false)
    var width: Double,

    @Column(nullable = false)
    var height: Double,
) : Persistent()
