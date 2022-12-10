package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.imageserver.service.StorageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.ThreadLocalRandom
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@RequestMapping("/v1/detection")
class DetectionController(
    private val storageService: StorageService,
) {
    @Operation(
        summary = "Get detection results", description = "Get detection results of which we get the chart data",
        security = [
            SecurityRequirement(name = "oauth2", scopes = ["detection:read"])
        ],
        tags = ["detection"]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
        ]
    )
    @GetMapping("/browse")
    @PreAuthorize("hasAuthority('detection:read')")
    fun browse(
        @Parameter(description = "Detection type, no filter if omitted") @Valid
        @RequestParam(required = false) type: DetectionType?,

        @Parameter(description = "Starting period, no filter if omitted") @Valid
        @RequestParam(required = false)
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted") @Valid
        @RequestParam(required = false)
        endDate: LocalDate?,

        @Parameter(description = "Location of camera, no filter if omitted") @Valid
        @RequestParam(required = false)
        location: String?,

        @Parameter(description = "Page of results") @Valid
        @RequestParam(required = false, defaultValue = "0")
        @Min(0)
        page: Int,

        @Parameter(description = "Size of results per page") @Valid
        @RequestParam(required = false, defaultValue = "1000")
        @Min(1)
        size: Int,
    ): ResponseEntity<WebResponse<Page<DetectionResponse>>> {

        val startInstant = startDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant() ?: Instant.EPOCH
        val endInstant = endDate?.plusDays(1)?.atStartOfDay(ZoneId.systemDefault())?.toInstant() ?: Instant.now()

        val content = mutableListOf<DetectionResponse>()
        for (i in 1..size) {
            val instant = randomInstantBetween(startInstant, endInstant)
            val date = LocalDate.ofInstant(instant, ZoneId.systemDefault())
            val annotations = detectionAnnotations()
            content.add(
                DetectionResponse(
                    date = date,
                    instant = instant,
                    location = desas[ThreadLocalRandom.current().nextInt(desas.size)],
                    cameraName = "Camera " + ThreadLocalRandom.current().nextInt(size),
                    type = type ?: DetectionType.values()[ThreadLocalRandom.current()
                        .nextInt(DetectionType.values().size)],
                    value = 3,
                    imageSrc = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/v1/image/${storageService.dummyId()}").toUriString(),
                    annotations = annotations
                )
            )
        }
        return ResponseEntity.ok(
            WebResponse(
                success = true,
                message = "ok",
                data = PageImpl(
                    content,
                    PageRequest.of(page, size),
                    size * 100L
                )
            )
        )
    }
}
