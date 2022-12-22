package com.katalisindonesia.banyuwangi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.envers.Audited
import org.hibernate.envers.NotAudited
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    indexes = [
        Index(name = "camera_isactive_idx", columnList = "isActive"),
        Index(name = "camera_isliveview_idx", columnList = "isLiveView"),
    ]
)
@Audited
data class Camera(
    @Column(nullable = true, unique = true)
    var vmsCameraIndexCode: String? = null,

    @Column(nullable = true, unique = false)
    @Enumerated(EnumType.STRING)
    var vmsType: VmsType? = null,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false)
    var location: String,

    @Column(nullable = false)
    var latitude: Float = 0F,

    @Column(nullable = false)
    var longitude: Float = 0F,

    @Column(nullable = false)
    var host: String = "",

    @Column(nullable = false)
    var httpPort: Int = 80,

    var rtspPort: Int = 554,

    @Column(nullable = false)
    var channel: Int = 1,

    @Column(nullable = true)
    var captureQualityChannel: String? = "01",

    @Column(nullable = false)
    var userName: String = "",

    @Column(nullable = false)
    var password: String = "",

    @Column(nullable = false)
    var isActive: Boolean = true,

    @Column(nullable = false)
    var isStreetvendor: Boolean = false,

    @Column(nullable = false)
    var isTraffic: Boolean = false,

    @Column(nullable = false)
    var isCrowd: Boolean = false,

    @Column(nullable = false)
    var isTrash: Boolean = false,

    @Column(nullable = false)
    var isFlood: Boolean = false,

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var type: CameraType? = CameraType.HIKVISION,

    var label: String? = null,

    @JsonIgnore
    @NotAudited
    var interior: CameraInterior? = CameraInterior(),

    var alarmSetting: AlarmSetting? = null,
) : Persistent() {

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
