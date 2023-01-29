package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.Total
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Optional
import java.util.UUID

interface TotalRepo : BaseRepository<Total, UUID> {
    fun findByTypeEqualsAndChronoUnitEqualsAndInstantEquals(
        type: DetectionType,
        chronoUnit: ChronoUnit,
        instant: Instant,
    ): Optional<Total>
}
