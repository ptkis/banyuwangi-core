package com.katalisindonesia.banyuwangi.model

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class CameraInterior(
    var isLoginSucceeded: Boolean? = null,
    var isLiveView: Boolean? = true,

    @Enumerated(EnumType.STRING)
    var lastCaptureMethod: CaptureMethod? = null,

    @Column(nullable = true)
    var lastCaptureInstant: Instant? = null,

    @Column
    var isPing: Boolean? = false,

    @Column
    var pingResponseTimeSec: Double? = null,

    @Column(length = 512)
    var pingRawData: String? = null,

    var pingLast: Instant? = null,

    var liveViewUrl: String? = null,

    var liveViewHash: String? = null,
)
