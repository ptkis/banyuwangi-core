package com.katalisindonesia.banyuwangi.model

import java.time.Instant
import java.util.UUID
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
        Index(
            name = "snapshotcount_snapshotcreated_idx",
            columnList = "snapshotCreated",
        ),
        Index(
            name = "snapshotcount_type_idx",
            columnList = "type",
        ),
        Index(
            name = "snapshotcount_snapshotcameralocation_idx",
            columnList = "snapshotCameraLocation",
            unique = false,
        ),
    ]
)
data class SnapshotCount(
    @ManyToOne
    @JoinColumn(nullable = false)
    var snapshot: Snapshot,

    @Column(nullable = false)
    var snapshotCreated: Instant = snapshot.created,

    @Column(nullable = false, unique = false, columnDefinition = "binary(16)")
    var snapshotImageId: UUID = snapshot.imageId,

    var snapshotCameraName: String = snapshot.camera.name,
    var snapshotCameraLocation: String = snapshot.camera.location,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: DetectionType,

    @Column(nullable = false, name = "m_value")
    var value: Int,
) : Persistent()
