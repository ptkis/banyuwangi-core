package com.katalisindonesia.banyuwangi.controller

import com.fasterxml.jackson.annotation.JsonIgnore
import com.katalisindonesia.banyuwangi.model.Camera

data class LiveCamera(
    @JsonIgnore
    private val camera: Camera,
) {
    val vmsCameraIndexCode = camera.vmsCameraIndexCode
    val vmsType = camera.vmsType
    val name = camera.name
    val location = camera.location
    val latitude = camera.latitude
    val longitude = camera.longitude
    val isActive = camera.isActive
    val isStreetvendor = camera.isStreetvendor
    val isTraffic = camera.isTraffic
    val isCrowd = camera.isCrowd
    val isTrash = camera.isTrash
    val isFlood = camera.isFlood
    val label = camera.label
    val liveViewUrl = camera.interior.liveViewUrl
}
