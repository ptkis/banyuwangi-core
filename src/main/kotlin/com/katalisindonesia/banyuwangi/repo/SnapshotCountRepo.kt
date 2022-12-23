package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SnapshotCountRepo : JpaRepository<SnapshotCount, UUID>, JpaSpecificationExecutor<SnapshotCount> {
    fun getBySnapshotImageIdEqualsAndTypeEqualsAndValueEquals(
        snapshotImageId: UUID,
        type: DetectionType,
        value: Int,
        pageable: Pageable,
    ): Page<SnapshotCount>
}
