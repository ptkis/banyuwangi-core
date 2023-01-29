package com.katalisindonesia.banyuwangi.model

import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    indexes = [
        Index(name = "total_typeinstantchronounit_key", columnList = "type,instant,chronoUnit", unique = true),
    ]
)
data class Total(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: DetectionType,

    @Column(nullable = false)
    var instant: Instant,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var chronoUnit: ChronoUnit,

    @Column(nullable = false)
    var countAlarmValue: Long = 0L,

    @Column(nullable = false)
    var countValue: Long = 0L,

    @Column(nullable = false)
    var sumValue: Long = 0L,

    @Column(nullable = false)
    var maxValue: Long = 0L,

    @Column(nullable = false)
    var avgValue: Long = 0L,
) : Persistent()
