package com.katalisindonesia.banyuwangi.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class SnapshotCount(
    @ManyToOne
    @JoinColumn(nullable = false)
    var snapshot: Snapshot,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: DetectionType,

    @Column(nullable = false, name = "m_value")
    var value: Int,
) : Persistent()
