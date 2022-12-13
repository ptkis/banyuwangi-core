package com.katalisindonesia.banyuwangi.model

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class Snapshot(
    @Column(nullable = false)
    val imageId: UUID,

    @ManyToOne
    @JoinColumn(nullable = false)
    val camera: Camera,

    @Column(nullable = false)
    val length: Long,
) : Persistent()
