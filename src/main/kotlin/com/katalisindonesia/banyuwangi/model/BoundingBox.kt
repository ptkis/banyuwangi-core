package com.katalisindonesia.banyuwangi.model

import javax.persistence.Column
import javax.persistence.Entity

@Entity
data class BoundingBox(

    // left is sql keyword
    @Column(nullable = false)
    var x: Float,

    @Column(nullable = false)
    var y: Float,

    @Column(nullable = false)
    var width: Float,

    @Column(nullable = false)
    var height: Float,
) : Persistent()
