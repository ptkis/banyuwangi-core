package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.Snapshot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface SnapshotRepo : JpaRepository<Snapshot, UUID> {
    @Query("select t from Snapshot t where t.imageId = :uuid")
    fun getWithImageId(uuid: UUID): Optional<Snapshot>
}
