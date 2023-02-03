package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SnapshotCountRepo : BaseRepository<SnapshotCount, UUID> {
    fun getBySnapshotImageIdEqualsAndTypeEqualsAndValueEquals(
        snapshotImageId: UUID,
        type: DetectionType,
        value: Int,
        pageable: Pageable,
    ): Slice<SnapshotCount>

    @Query("select t from SnapshotCount t where t.isImageDeleted = :isImageDeleted")
    fun findWithIsImageDeleted(isImageDeleted: Boolean, pageable: Pageable): Slice<SnapshotCount>
}
