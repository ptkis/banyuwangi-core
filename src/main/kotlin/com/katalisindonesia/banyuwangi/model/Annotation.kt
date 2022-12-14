package com.katalisindonesia.banyuwangi.model

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(
    indexes = [
        Index(name = "annotation_createdtype_idx", columnList = "created,type", unique = false)
    ]
)
data class Annotation(
    @ManyToOne
    @JoinColumn(nullable = false)
    var snapshot: Snapshot,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: DetectionType,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(nullable = false)
    var boundingBox: BoundingBox,

    @Column(nullable = false)
    var score: Double,

) : Persistent()
