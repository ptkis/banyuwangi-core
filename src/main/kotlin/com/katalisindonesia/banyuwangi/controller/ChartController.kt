package com.katalisindonesia.banyuwangi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
import java.time.ZonedDateTime
import javax.validation.Valid

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


}

