package com.katalisindonesia.banyuwangi.controller

import au.com.console.jpaspecificationdsl.and
import au.com.console.jpaspecificationdsl.equal
import au.com.console.jpaspecificationdsl.greaterThanOrEqualTo
import au.com.console.jpaspecificationdsl.lessThan
import au.com.console.jpaspecificationdsl.lessThanOrEqualTo
import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.Annotation
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import com.katalisindonesia.banyuwangi.repo.AnnotationRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotCountRepo
import com.katalisindonesia.imageserver.service.StorageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.format.annotation.DateTimeFormat
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
import java.util.concurrent.atomic.AtomicBoolean
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@RequestMapping("/v1/detection")
class DetectionController(
    private val storageService: StorageService,
    private val snapshotCountRepo: SnapshotCountRepo,
    private val annotationRepo: AnnotationRepo,
    private val appProperties: AppProperties,
) {
    private val productionMode = AtomicBoolean()

    @Operation(
        summary = "Get detection results",
        description = "Get detection results of which we get the chart data",
        security = [SecurityRequirement(name = "oauth2", scopes = ["detection:read"])],
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
        @Parameter(description = "Detection type, no filter if omitted")
        @Valid
        @RequestParam(required = false)
        type: DetectionType?,

        @Parameter(description = "Starting period, no filter if omitted")
        @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,

        @Parameter(description = "Ending period, no filter if omitted")
        @Valid
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?,

        @Parameter(description = "Location of camera, no filter if omitted")
        @Valid
        @RequestParam(required = false)
        location: String?,

        @Parameter(description = "Page of results") @Valid @RequestParam(
            required = false,
            defaultValue = "0"
        )
        @Min(0)
        page: Int,

        @Parameter(description = "Size of results per page") @Valid
        @RequestParam(
            required = false,
            defaultValue = "1000"
        )
        @Min(1)
        size: Int,
    ): ResponseEntity<WebResponse<Page<DetectionResponse>>> {

        val delegate = calcDelegate()
        val content = delegate(startDate, endDate, type, location, page, size)
        return ResponseEntity.ok(
            WebResponse(
                success = true, message = "ok", data = content
            )
        )
    }

    private fun calcDelegate() = if (productionMode.get()) {
        ::realResponses
    } else {
        productionMode.set(snapshotCountRepo.count() > 0)
        if (productionMode.get()) {
            ::realResponses
        } else {
            ::dummyResponses
        }
    }

    private fun realResponses(
        startDate: LocalDate?,
        endDate: LocalDate?,
        type: DetectionType?,
        location: String?,
        page: Int,
        size: Int,
    ): Page<DetectionResponse> {
        val countSpecs = mutableListOf<Specification<SnapshotCount>>()
        val annotationSpecs = mutableListOf<Specification<Annotation>>()
        if (startDate != null) {
            countSpecs.add(
                SnapshotCount::snapshotCreated.greaterThanOrEqualTo(
                    startDate.atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant()
                )
            )
            annotationSpecs.add(
                Annotation::snapshotCreated.greaterThanOrEqualTo(
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
            annotationSpecs.add(
                Annotation::snapshotCreated.lessThan(
                    endDate.plusDays(1).atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant()
                )
            )
        }
        if (type != null) {
            countSpecs.add(
                SnapshotCount::type.equal(type)
            )
            annotationSpecs.add(
                Annotation::type.equal(type)
            )
        }
        if (location != null) {
            countSpecs.add(
                SnapshotCount::snapshotCameraLocation.equal(location)
            )
            annotationSpecs.add(
                Annotation::snapshotCameraLocation.equal(location)
            )
        }

        val counts = snapshotCountRepo.findAll(
            and(countSpecs),
            PageRequest.of(
                page, size,
                Sort.by(
                    SnapshotCount::snapshotCreated.name,
                    SnapshotCount::type.name,
                )
            )
        )
        if (!counts.isEmpty) {
            annotationSpecs.add(
                Annotation::snapshotCreated.greaterThanOrEqualTo(
                    counts.first().snapshotCreated
                )
            )
            annotationSpecs.add(
                Annotation::snapshotCreated.lessThanOrEqualTo(
                    counts.last().snapshotCreated
                )
            )
        }
        val annotationMap = annotationRepo.findAll(and(annotationSpecs)).groupBy {
            Pair(it.snapshotImageId, it.type)
        }

        return counts.map {
            DetectionResponse(
                date = LocalDate.ofInstant(it.snapshotCreated, ZoneId.systemDefault()),
                instant = it.snapshotCreated,
                location = it.snapshotCameraLocation,
                cameraName = it.snapshotCameraName,
                type = it.type,
                value = it.value,
                imageSrc = storageService.uri(it.snapshotImageId).toString(),
                annotations = annotationMap[Pair(it.snapshotImageId, it.type)] ?: emptyList()
            )
        }
    }

    private fun dummyResponses(
        startDate: LocalDate?,
        endDate: LocalDate?,
        type: DetectionType?,
        location: String?,
        page: Int,
        size: Int
    ): Page<DetectionResponse> {
        val startInstant = startDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant() ?: Instant.EPOCH
        val endInstant = endDate?.plusDays(1)?.atStartOfDay(ZoneId.systemDefault())?.toInstant() ?: Instant.now()

        val content = mutableListOf<DetectionResponse>()
        for (i in 1..size) {
            val instant = randomInstantBetween(startInstant, endInstant)
            val date = LocalDate.ofInstant(instant, ZoneId.systemDefault())
            val type1 = type ?: DetectionType.values()[
                ThreadLocalRandom.current()
                    .nextInt(DetectionType.values().size)
            ]
            val annotations = detectionAnnotations(type1, storageService.dummyId())
            content.add(
                DetectionResponse(
                    date = date,
                    instant = instant,
                    location = location ?: desas[ThreadLocalRandom.current().nextInt(desas.size)],
                    cameraName = "Camera $i",
                    type = type1,
                    value = 3,
                    imageSrc = ServletUriComponentsBuilder.fromUri(appProperties.baseUri)
                        .path("/v1/image/${storageService.dummyId()}").toUriString(),
                    annotations = annotations
                )
            )
        }
        return PageImpl(
            content, PageRequest.of(page, size), size * size.toLong()
        )
    }

    fun resetProductionMode() {
        productionMode.set(false)
    }
}
