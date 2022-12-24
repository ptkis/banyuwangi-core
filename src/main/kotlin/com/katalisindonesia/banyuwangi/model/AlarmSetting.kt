package com.katalisindonesia.banyuwangi.model

import javax.persistence.Embeddable

@Embeddable
data class AlarmSetting(
    var maxFlood: Int? = null,
    var maxTrash: Int? = null,
    var maxStreetvendor: Int? = null,
    var maxCrowd: Int? = null,
    var maxTraffic: Int? = null,
) {
    fun max(type: DetectionType) = when (type) {
        DetectionType.FLOOD -> maxFlood
        DetectionType.TRASH -> maxTrash
        DetectionType.STREETVENDOR -> maxStreetvendor
        DetectionType.CROWD -> maxCrowd
        DetectionType.TRAFFIC -> maxTraffic
    }
}
