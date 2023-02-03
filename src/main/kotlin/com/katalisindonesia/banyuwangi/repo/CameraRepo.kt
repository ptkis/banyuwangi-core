package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.Camera
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

@Repository
interface CameraRepo : JpaRepository<Camera, UUID>, JpaSpecificationExecutor<Camera> {

    fun getCameraByVmsCameraIndexCode(vmsCameraIndexCode: String): Optional<Camera>

    @Query("select t from Camera t where t.isActive = :active")
    fun findWithIsActive(active: Boolean, pageable: Pageable): Page<Camera>

    @Modifying
    @Transactional
    @Query("delete from Camera")
    fun deleteAllWithQuery()
}
