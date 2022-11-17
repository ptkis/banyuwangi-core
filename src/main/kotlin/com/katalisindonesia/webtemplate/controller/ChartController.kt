package com.katalisindonesia.webtemplate.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.*
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
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
class ChartController {
    @Operation(
        summary = "Get flood chart", description = "Get flood chart data", security = [
            SecurityRequirement(name = "oauth2", scopes = ["chart:read"])], tags = ["chart"]
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

        @Parameter(description = "Camera name, no filter if omitted") @Valid
        @RequestParam(required = false)
        camera: String?,
    ): ChartData<ZonedDateTime> {
        val labels = dummyLabels(startDate, endDate)
        return ChartData(
            seriesNames = desas,
            labels = labels,
            data = dummyData(desas, labels)
        )
    }

    private fun dummyLabels(startDate: LocalDate?, endDate: LocalDate?): List<ZonedDateTime> {
        val list = mutableListOf<ZonedDateTime>()
        var date: ZonedDateTime = startDate?.atStartOfDay(ZoneId.systemDefault()) ?: ZonedDateTime.now().minusDays(30)
        val end = endDate?.atStartOfDay(ZoneId.systemDefault()) ?: ZonedDateTime.now()
        for (i in 0..5000) {
            list.add(date)

            date = date.plusHours(1)
            if (date > end) {
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
                list.add((Math.random() * 10000L).toLong())
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


