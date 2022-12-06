package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.Camera
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface CameraRepo : JpaRepository<Camera, UUID> {
    fun getCameraByVmsCameraIndexCode(vmsCameraIndexCode: String): Optional<Camera>
}
