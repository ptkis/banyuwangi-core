package com.katalisindonesia.banyuwangi.controller

import au.com.console.jpaspecificationdsl.and
import au.com.console.jpaspecificationdsl.get
import au.com.console.jpaspecificationdsl.greaterThanOrEqualTo
import au.com.console.jpaspecificationdsl.join
import au.com.console.jpaspecificationdsl.lessThan
import au.com.console.jpaspecificationdsl.where
import com.katalisindonesia.banyuwangi.model.Alarm
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import com.katalisindonesia.banyuwangi.repo.AlarmRepo
import com.katalisindonesia.imageserver.service.StorageService
import io.github.sercasti.tracing.Traceable
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.jpa.domain.Specification
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.ZoneId
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@RequestMapping("/v1/alarm")
@Tag(name = "alarm", description = "Alarm")
class AlarmController(
    private val alarmRepo: AlarmRepo,
    private val storageService: StorageService,
) {

    @GetMapping("/list")
    @Traceable
    @PreAuthorize("hasAuthority('alarm:read')")
    @Operation(
        summary = "Get all alarms",
        description = "Get all alarms",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
            ApiResponse(responseCode = "400", description = "Invalid request parameter"),
            ApiResponse(
                responseCode = "403",
                description = "You do not have required permission. Check token and scope.",
            ),
        ],
    )
    fun list(
        @RequestParam(required = false)
        detectionType: Set<DetectionType>?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        beginning: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        ending: LocalDate?,

        @Parameter(description = "Sorted property")
        @Valid
        @RequestParam(
            required = false,
            defaultValue = "CREATED",
        )
        sort: AlarmSortProperty,

        @Parameter(description = "Sort direction, ascending if omitted")
        @Valid
        @RequestParam(
            required = false,
            defaultValue = "DESC",
        )
        direction: Direction,

        @Parameter(description = "Page number, defaults to 0")
        @Valid
        @RequestParam(
            required = false,
            defaultValue = "0",
        )
        @Min(0L)
        page: Int,

        @Parameter(description = "Size of a page")
        @Valid
        @RequestParam(
            required = false,
            defaultValue = "1000",
        )
        @Min(0L)
        size: Int,
    ): ResponseEntity<WebResponse<List<DetectionResponse>>> {
        val specs = specs(detectionType, beginning, ending)
        val page1 = alarmRepo.findAll(
            and(specs),
            PageRequest.of(
                page,
                size,
                Sort.by(
                    direction,
                    sort.propertyName,
                ),
            ),
        )
        val zone = ZoneId.systemDefault()
        return ResponseEntity.ok(
            WebResponse(
                success = true,
                message = "ok",
                data = page1.map {
                    val camera = it.snapshotCount.snapshot.camera
                    DetectionResponse(
                        date = it.created.atZone(zone).toLocalDate(),
                        instant = it.created,
                        location = camera.location,
                        cameraName = camera.name,
                        type = it.snapshotCount.type,
                        value = it.snapshotCount.value,
                        imageSrc = storageService.uri(
                            it.snapshotCount.snapshotImageId,
                        )
                            .toString(),
                        annotations = emptyList(),
                    )
                }
            )
        )
    }

    @GetMapping("/count")
    @Traceable
    @PreAuthorize("hasAuthority('alarm:read')")
    @Operation(
        summary = "Count all alarms",
        description = "Count all alarms",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
            ApiResponse(responseCode = "400", description = "Invalid request parameter"),
            ApiResponse(
                responseCode = "403",
                description = "You do not have required permission. Check token and scope.",
            ),
        ],
    )
    fun count(
        @RequestParam(required = false)
        detectionType: Set<DetectionType>?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        beginning: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        ending: LocalDate?,
    ): ResponseEntity<WebResponse<Long>> {
        val specs = specs(detectionType, beginning, ending)
        val page1 = alarmRepo.countAll(
            and(specs),
        )
        return ResponseEntity.ok(
            WebResponse(
                success = true,
                message = "ok",
                data = page1,
            ),
        )
    }

    private fun specs(
        detectionType: Set<DetectionType>?,
        beginning: LocalDate?,
        ending: LocalDate?,
    ): MutableList<Specification<Alarm>> {
        val zone = ZoneId.systemDefault()
        val specs = mutableListOf<Specification<Alarm>>()
        if (!detectionType.isNullOrEmpty()) {
            specs.add(
                where {
                    it.join(Alarm::snapshotCount)
                        .get(SnapshotCount::type).`in`(
                            detectionType
                        )
                }
            )
        }
        if (beginning != null) {
            specs.add(Alarm::created.greaterThanOrEqualTo(beginning.atStartOfDay(zone).toInstant()))
        }
        if (ending != null) {
            specs.add(Alarm::created.lessThan(ending.plusDays(1).atStartOfDay(zone).toInstant()))
        }

        return specs
    }
}

enum class AlarmSortProperty(val propertyName: String) {
    CREATED(Alarm::created.name),
}
