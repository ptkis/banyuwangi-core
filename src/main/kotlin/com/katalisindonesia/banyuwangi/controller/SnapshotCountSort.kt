package com.katalisindonesia.banyuwangi.controller

import com.google.common.base.CaseFormat

enum class SnapshotCountSort {
    SNAPSHOT_CREATED,
    SNAPSHOT_CAMERA_NAME,
    SNAPSHOT_CAMERA_LOCATION,
    SNAPSHOT_CAMERA_LONGITUDE,
    SNAPSHOT_CAMERA_LATITUDE,
    TYPE,
    MAX_VALUE,
    VALUE,
    ;

    fun asPropertyName() = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name)
}
