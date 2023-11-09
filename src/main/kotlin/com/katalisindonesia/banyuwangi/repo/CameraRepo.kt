package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.Camera
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

@Repository
interface CameraRepo : BaseRepository<Camera, UUID> {

    fun getCameraByVmsCameraIndexCode(vmsCameraIndexCode: String): Optional<Camera>

    @Query("select t from Camera t where t.isActive = :active")
    fun findWithIsActive(active: Boolean, pageable: Pageable): Page<Camera>

    @Modifying
    @Transactional
    @Query("delete from Camera")
    fun deleteAllWithQuery()

    @Query(
        "select t.vmsCameraIndexCode from Camera t " +
            "where t.vmsCameraIndexCode is not null " +
            "order by t.vmsCameraIndexCode"
    )
    fun findVmsCameraIndexCode(): List<String>

    @Query(
        "select t.vmsCameraIndexCode from Camera t where " +
            "t.vmsCameraIndexCode is not null and t.face is not null " +
            "and t.face=:face order by t.vmsCameraIndexCode"
    )
    fun findVmsCameraIndexCodeWithFace(face: Boolean): List<String>

    @Query(
        "select distinct t.location from Camera t " +
            "where t.location is not null " +
            "and (:keyword ='' or t.location like :keyword) order by t.location"
    )
    fun findCameraLocations(keyword: String, pageable: Pageable): List<String>
}
