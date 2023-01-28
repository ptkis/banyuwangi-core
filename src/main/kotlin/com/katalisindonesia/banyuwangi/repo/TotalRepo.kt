package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.Total
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Optional
import java.util.UUID

interface TotalRepo : JpaRepository<Total, UUID>, JpaSpecificationExecutor<Total> {
    fun findByTypeEqualsAndChronoUnitEqualsAndInstantEquals(
        type: DetectionType,
        chronoUnit: ChronoUnit,
        instant: Instant,
    ): Optional<Total>
}
