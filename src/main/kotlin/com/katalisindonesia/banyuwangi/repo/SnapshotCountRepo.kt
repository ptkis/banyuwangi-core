package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.SnapshotCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SnapshotCountRepo : JpaRepository<SnapshotCount, UUID>
