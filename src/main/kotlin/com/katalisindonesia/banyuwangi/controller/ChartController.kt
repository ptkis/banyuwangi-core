package com.katalisindonesia.banyuwangi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.validation.Valid

private val desas = listOf(
    "Kampung Mandar",
    "Kampung Melayu",
    "Karangrejo",
    "Kebalenan",
    "Kepatihan",
    "Kertosari",
    "Lateng",
    "Pakis",
    "Panderejo",
    "Penganjuran",
    "Pengantigan",
    "Singonegaran",
    "Singotrunan",
    "Sobo",
    "Sumber Rejo",
    "Taman Baru",
    "Temenggungan",
    "Tukang Kayu",
)

@RestController
@RequestMapping("/v1/chart")
@Tag(name = "chart", description = "Chart")
@PreAuthorize("hasAuthority('chart')")
class ChartController {
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
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted") @Valid
        @RequestParam(required = false)
        endDate: LocalDate?,

        @Parameter(description = "Location of camera, no filter if omitted") @Valid
        @RequestParam(required = false)
        location: String?,
    ): ChartData<ZonedDateTime> {
        val labels = dummyLabels(startDate, endDate)
        val seriesNames = desas.filter { location == null || location == it }
        return ChartData(
            seriesNames = seriesNames,
            labels = labels,
            data = dummyData(seriesNames, labels)
        )
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
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted") @Valid
        @RequestParam(required = false)
        endDate: LocalDate?,

        @Parameter(description = "Location of camera, no filter if omitted") @Valid
        @RequestParam(required = false)
        location: String?,
    ): ChartData<ZonedDateTime> {
        val labels = dummyLabels(startDate, endDate)
        val seriesNames = desas.filter { location == null || location == it }
        return ChartData(
            seriesNames = seriesNames,
            labels = labels,
            data = dummyData(seriesNames, labels)
        )
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
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted") @Valid
        @RequestParam(required = false)
        endDate: LocalDate?,

        @Parameter(description = "Location of camera, no filter if omitted") @Valid
        @RequestParam(required = false)
        location: String?,
    ): ChartData<ZonedDateTime> {
        val labels = dummyLabels(startDate, endDate)
        val seriesNames = desas.filter { location == null || location == it }
        return ChartData(
            seriesNames = seriesNames,
            labels = labels,
            data = dummyData(seriesNames, labels)
        )
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
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted") @Valid
        @RequestParam(required = false)
        endDate: LocalDate?,

        @Parameter(description = "Location of camera, no filter if omitted") @Valid
        @RequestParam(required = false)
        location: String?,
    ): ChartData<ZonedDateTime> {
        val labels = dummyLabels(startDate, endDate)
        val seriesNames = desas.filter { location == null || location == it }
        return ChartData(
            seriesNames = seriesNames,
            labels = labels,
            data = dummyData(seriesNames, labels)
        )
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
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted") @Valid
        @RequestParam(required = false)
        endDate: LocalDate?,

        @Parameter(description = "Location of camera, no filter if omitted") @Valid
        @RequestParam(required = false)
        location: String?,
    ): ChartData<ZonedDateTime> {
        val labels = dummyLabels(startDate, endDate)
        val seriesNames = desas.filter { location == null || location == it }
        return ChartData(
            seriesNames = seriesNames,
            labels = labels,
            data = dummyData(seriesNames, labels)
        )
    }

    private val defaultDays = 30L
    private val dummyRange = 5000L
    private val dummyMaxValue = 10000L

    private fun dummyLabels(startDate: LocalDate?, endDate: LocalDate?): List<ZonedDateTime> {
        val list = mutableListOf<ZonedDateTime>()
        val date: ZonedDateTime = startDate?.atStartOfDay(ZoneId.systemDefault()) ?: ZonedDateTime.now().minusDays(
            defaultDays
        )
        val end = endDate?.atStartOfDay(ZoneId.systemDefault()) ?: ZonedDateTime.now()
        for (i in 0L..dummyRange) {
            val current = date.plusHours(i)
            list.add(current)

            if (current > end) {
                break
            }
        }
        return list
    }

    private fun <T> dummyData(seriesNames: List<String>, labels: List<T>): Map<String, List<Long>> {
        val map = mutableMapOf<String, List<Long>>()

        for (name in seriesNames) {
            val list = mutableListOf<Long>()

            for (i in 1..labels.size) {
                list.add((Math.random() * dummyMaxValue).toLong())
            }

            map[name] = list
        }

        return map
    }
}

data class ChartData<T>(
    @Schema(description = "List of series names")
    val seriesNames: List<String>,
    @Schema(description = "Series labels")
    val labels: List<T>,
    @Schema(description = "Data of the series")
    val data: Map<String, List<Long>>
)
