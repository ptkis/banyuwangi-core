package com.katalisindonesia.banyuwangi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant
import java.util.UUID
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
        Index(
            name = "annotation_snapshotcreated_idx",
            columnList = "snapshotCreated",
            unique = false,
        ),
        Index(
            name = "annotation_type_idx",
            columnList = "type",
            unique = false,
        ),
        Index(
            name = "annotation_snapshotcameralocation_idx",
            columnList = "snapshotCameraLocation",
            unique = false,
        ),
    ]
)
data class Annotation(
    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    var snapshot: Snapshot,

    @Column(nullable = false)
    var snapshotCreated: Instant = snapshot.created,

    @Column(nullable = false, unique = true, columnDefinition = "binary(16)")
    var snapshotImageId: UUID = snapshot.imageId,

    @Column(nullable = false)
    var snapshotCameraLocation: String = snapshot.camera.location,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: DetectionType,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(nullable = false)
    var boundingBox: BoundingBox,

    @Column(nullable = false)
    var confidence: Double,

) : Persistent()
