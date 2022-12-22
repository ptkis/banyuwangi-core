package com.katalisindonesia.banyuwangi.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.Min

@Entity
data class Alarm(
    @Column(nullable = false)
    @Min(0)
    var maxValue: Int,

    @ManyToOne
    @JoinColumn(nullable = false)
    var snapshotCount: SnapshotCount,

) : Persistent()
