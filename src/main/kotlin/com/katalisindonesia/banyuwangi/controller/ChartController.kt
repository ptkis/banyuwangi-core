package com.katalisindonesia.banyuwangi.controller

import au.com.console.jpaspecificationdsl.and
import au.com.console.jpaspecificationdsl.equal
import au.com.console.jpaspecificationdsl.greaterThanOrEqualTo
import au.com.console.jpaspecificationdsl.lessThan
import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import com.katalisindonesia.banyuwangi.model.Total
import com.katalisindonesia.banyuwangi.repo.SnapshotCountRepo
import com.katalisindonesia.banyuwangi.repo.TotalRepo
import com.katalisindonesia.banyuwangi.util.toCachedResponseEntity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/v1/chart")

@Tag(name = "chart", description = "Chart")
@PreAuthorize("hasAuthority('chart')")
class ChartController(
    private val snapshotCountRepo: SnapshotCountRepo,
    private val totalRepo: TotalRepo,
    private val appProperties: AppProperties,
) {
    private val helper = ChartHelper()

    @Operation(
        summary = "Get flood chart", description = "Get flood chart data",
        security = [
            SecurityRequirement(name = "oauth2", scopes = ["chart:read"])
        ],
        tags = ["chart"]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
        ]
    )
    @GetMapping("/flood", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun flood(
        @Parameter(description = "Starting period, no filter if omitted") @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted") @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?,

        @Parameter(description = "Location of camera, no filter if omitted") @Valid
        @RequestParam(required = false)
        location: String?,

        @Parameter(description = "Page number") @Valid
        @Min(0)
        @RequestParam(required = false, defaultValue = "0")
        page: Int = 0,

        @Parameter(description = "How many results per page") @Valid
        @RequestParam(required = false, defaultValue = "\${dashboard.app.defaultSize}")
        @Min(0)
        @Max(10000)
        size: Int = 1000,
    ): ResponseEntity<ChartData<ZonedDateTime>> {
        return helper.chartData(
            counts(
                startDate = startDate,
                endDate = endDate,
                location = location,
                type = DetectionType.FLOOD,
                page = page,
                size = size,
            )
        ).toCachedResponseEntity(appProperties.chartCacheSeconds)
    }

    @Operation(
        summary = "Get trash chart", description = "Get trash chart data",
        security = [
            SecurityRequirement(name = "oauth2", scopes = ["chart:read"])
        ],
        tags = ["chart"]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
        ]
    )
    @GetMapping("/trash", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun trash(
        @Parameter(description = "Starting period, no filter if omitted") @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted") @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?,

        @Parameter(description = "Location of camera, no filter if omitted") @Valid
        @RequestParam(required = false)
        location: String?,

        @Parameter(description = "Page number") @Valid
        @Min(0)
        @RequestParam(required = false, defaultValue = "0")
        page: Int = 0,

        @Parameter(description = "How many results per page") @Valid
        @RequestParam(required = false, defaultValue = "\${dashboard.app.defaultSize}")
        @Min(0)
        @Max(10000)
        size: Int = 1000,
    ): ResponseEntity<ChartData<ZonedDateTime>> {
        return helper.chartData(
            counts(
                startDate = startDate,
                endDate = endDate,
                location = location,
                type = DetectionType.TRASH,
                page = page,
                size = size,
            )
        ).toCachedResponseEntity(appProperties.chartCacheSeconds)
    }

    @Operation(
        summary = "Get street vendor chart", description = "Get street vendor chart data",
        security = [
            SecurityRequirement(name = "oauth2", scopes = ["chart:read"])
        ],
        tags = ["chart"]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
        ]
    )
    @GetMapping("/streetvendor", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun streetvendor(
        @Parameter(description = "Starting period, no filter if omitted") @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted") @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?,

        @Parameter(description = "Location of camera, no filter if omitted") @Valid
        @RequestParam(required = false)
        location: String?,

        @Parameter(description = "Page number") @Valid
        @Min(0)
        @RequestParam(required = false, defaultValue = "0")
        page: Int = 0,

        @Parameter(description = "How many results per page") @Valid
        @RequestParam(required = false, defaultValue = "\${dashboard.app.defaultSize}")
        @Min(0)
        @Max(10000)
        size: Int = 1000,
    ): ResponseEntity<ChartData<ZonedDateTime>> {
        return helper.chartData(
            counts(
                startDate = startDate,
                endDate = endDate,
                location = location,
                type = DetectionType.STREETVENDOR,
                page = page,
                size = size,
            )
        ).toCachedResponseEntity(appProperties.chartCacheSeconds)
    }

    @Operation(
        summary = "Get crowd chart", description = "Get crowd chart data",
        security = [
            SecurityRequirement(name = "oauth2", scopes = ["chart:read"])
        ],
        tags = ["chart"]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
        ]
    )
    @GetMapping("/crowd", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun crowd(
        @Parameter(description = "Starting period, no filter if omitted") @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted") @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?,

        @Parameter(description = "Location of camera, no filter if omitted") @Valid
        @RequestParam(required = false)
        location: String?,

        @Parameter(description = "Page number") @Valid
        @Min(0)
        @RequestParam(required = false, defaultValue = "0")
        page: Int = 0,

        @Parameter(description = "How many results per page") @Valid
        @RequestParam(required = false, defaultValue = "\${dashboard.app.defaultSize}")
        @Min(0)
        @Max(10000)
        size: Int = 1000,
    ): ResponseEntity<ChartData<ZonedDateTime>> {
        return helper.chartData(
            counts(
                startDate = startDate,
                endDate = endDate,
                location = location,
                type = DetectionType.CROWD,
                page = page,
                size = size
            )
        ).toCachedResponseEntity(appProperties.chartCacheSeconds)
    }

    @Operation(
        summary = "Get traffic chart", description = "Get traffic chart data",
        security = [
            SecurityRequirement(name = "oauth2", scopes = ["chart:read"])
        ],
        tags = ["chart"]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
        ]
    )
    @GetMapping("/traffic", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun traffic(
        @Parameter(description = "Starting period, no filter if omitted") @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted") @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?,

        @Parameter(description = "Location of camera, no filter if omitted") @Valid
        @RequestParam(required = false)
        location: String?,

        @Parameter(description = "Page number") @Valid
        @Min(0)
        @RequestParam(required = false, defaultValue = "0")
        page: Int = 0,

        @Parameter(description = "How many results per page") @Valid
        @RequestParam(required = false, defaultValue = "\${dashboard.app.defaultSize}")
        @Min(0)
        @Max(10000)
        size: Int = 1000,
    ): ResponseEntity<ChartData<ZonedDateTime>> {
        return helper.chartData(
            counts(
                startDate = startDate,
                endDate = endDate,
                location = location,
                type = DetectionType.TRAFFIC,
                page = page,
                size = size
            )
        ).toCachedResponseEntity(appProperties.chartCacheSeconds)
    }

    @Operation(
        summary = "Get total chart", description = "Get total chart data",
        security = [
            SecurityRequirement(name = "oauth2", scopes = ["chart:read"])
        ],
        tags = ["chart"]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
        ]
    )
    @GetMapping("/total", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun total(
        @Parameter(description = "Detection type, required") @Valid
        @NotNull
        @RequestParam(required = true)
        type: DetectionType,

        @Parameter(description = "Starting period, last midnight if omitted") @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted") @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?,

        @Parameter(description = "Page number") @Valid
        @Min(0)
        @RequestParam(required = false, defaultValue = "0")
        page: Int = 0,

        @Parameter(description = "How many results per page") @Valid
        @RequestParam(required = false, defaultValue = "\${dashboard.app.defaultSize}")
        @Min(0)
        @Max(10000)
        size: Int = 1000,
    ): ResponseEntity<ChartData<ZonedDateTime>> {
        return helper.chartData(
            findTotals(
                startDate = startDate,
                endDate = endDate,
                type = type,
                page = page,
                size = size
            ),
            appProperties.totalPreferredProperty
        ).toCachedResponseEntity(appProperties.chartCacheSeconds)
    }

    private fun counts(
        startDate: LocalDate?,

        endDate: LocalDate?,

        location: String?,

        type: DetectionType?,
        page: Int = 0,
        size: Int = 1000,
    ): List<SnapshotCount> {
        val countSpecs = mutableListOf<Specification<SnapshotCount>>()

        if (startDate != null) {
            countSpecs.add(
                SnapshotCount::snapshotCreated.greaterThanOrEqualTo(
                    startDate.atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant()
                )
            )
        }
        if (endDate != null) {
            countSpecs.add(
                SnapshotCount::snapshotCreated.lessThan(
                    endDate.plusDays(1).atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant()
                )
            )
        }
        if (type != null) {
            countSpecs.add(
                SnapshotCount::type.equal(type)
            )
        }
        if (location != null) {
            countSpecs.add(
                SnapshotCount::snapshotCameraLocation.equal(location)
            )
        }

        return snapshotCountRepo.findAll(
            and(countSpecs),
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, SnapshotCount::snapshotCreated.name))
        ).reversed()
    }

    private fun findTotals(
        startDate: LocalDate?,

        endDate: LocalDate?,

        type: DetectionType?,
        page: Int = 0,
        size: Int = 1000,
    ): List<Total> {
        val countSpecs = mutableListOf<Specification<Total>>()

        countSpecs.add(
            Total::instant.greaterThanOrEqualTo(
                startDate?.atStartOfDay()
                    ?.atZone(ZoneId.systemDefault())?.toInstant() ?: ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                    .toInstant()
            )
        )
        if (endDate != null) {
            countSpecs.add(
                Total::instant.lessThan(
                    endDate.plusDays(1).atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant()
                )
            )
        }
        if (type != null) {
            countSpecs.add(
                Total::type.equal(type)
            )
        }
        countSpecs.add(Total::chronoUnit.equal(appProperties.totalTruncateChronoUnit))

        return totalRepo.findAll(
            and(countSpecs),
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, Total::instant.name))
        ).reversed()
    }
}
