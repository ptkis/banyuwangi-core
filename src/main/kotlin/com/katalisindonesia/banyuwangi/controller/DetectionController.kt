package com.katalisindonesia.banyuwangi.controller

import au.com.console.jpaspecificationdsl.and
import au.com.console.jpaspecificationdsl.equal
import au.com.console.jpaspecificationdsl.greaterThanOrEqualTo
import au.com.console.jpaspecificationdsl.isFalse
import au.com.console.jpaspecificationdsl.lessThan
import au.com.console.jpaspecificationdsl.lessThanOrEqualTo
import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.Annotation
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import com.katalisindonesia.banyuwangi.repo.AnnotationRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotCountRepo
import com.katalisindonesia.banyuwangi.util.toCachedWebResponseEntity
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
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.jpa.domain.Specification
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
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
        return delegate(
            startDate,
            endDate,
            type,
            location,
            page,
            size
        ).toCachedWebResponseEntity(appProperties.detectionCacheSeconds)
    }

    @GetMapping("/id/{snapshotImageId}")
    @Operation(
        summary = "Get detection results by snapshot image ids",
        description = "Get detection results of which we get the chart data",
        security = [SecurityRequirement(name = "oauth2", scopes = ["detection:read"])],
        tags = ["detection"]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
        ]
    )
    @PreAuthorize("hasAuthority('detection:read')")
    fun bySnapshotImageId(
        @Parameter(description = "Snapshot image id, required")
        @PathVariable snapshotImageId: UUID,

        @Parameter(description = "Type of the detection, required")
        @RequestParam type: DetectionType,

        @Parameter(description = "Value of the chart data, required")
        @RequestParam value: Int,
    ): ResponseEntity<WebResponse<DetectionResponse>> {
        val snapshotCountOpt = snapshotCountRepo.getBySnapshotImageIdEqualsAndTypeEqualsAndValueEquals(
            snapshotImageId = snapshotImageId,
            type = type,
            value = value,
            pageable = PageRequest.of(0, 1)
        )
        if (snapshotCountOpt.isEmpty) {
            return ResponseEntity.notFound()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                .build()
        }

        val annotations = annotationRepo.findBySnapshotImageIdEquals(snapshotImageId)

        val it = snapshotCountOpt.first()
        return ResponseEntity.ok()
            .cacheControl(cacheControl()).body(
                WebResponse(
                    success = true,
                    message = "ok",
                    data = DetectionResponse(
                        date = LocalDate.ofInstant(it.snapshotCreated, ZoneId.systemDefault()),
                        instant = it.snapshotCreated,
                        location = it.snapshotCameraLocation,
                        cameraName = it.snapshotCameraName,
                        type = it.type,
                        value = it.value,
                        imageSrc = storageService.uri(it.snapshotImageId).toString(),
                        annotations = annotations,
                        longitude = it.snapshot.camera.longitude.toBigDecimal(),
                        latitude = it.snapshot.camera.latitude.toBigDecimal(),
                    )
                )
            )
    }

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
    @GetMapping("/counts")
    @PreAuthorize("hasAuthority('detection:read')")
    fun counts(
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

        @Parameter(description = "Name of camera, no filter if omitted")
        @Valid
        @RequestParam(required = false)
        cameraName: String?,

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

        @Parameter(description = "Sort by")
        @RequestParam(required = false, defaultValue = "SNAPSHOT_CREATED")
        sort: SnapshotCountSort,

        @Parameter(description = "Direction of sort")
        @RequestParam(required = false, defaultValue = "DESC")
        direction: Direction,

    ): ResponseEntity<WebResponse<Page<SnapshotCount>>> {
        val specs = mutableListOf<Specification<SnapshotCount>>()
        if (type != null) {
            specs.add(SnapshotCount::type.equal(type))
        }
        if (startDate != null) {
            specs.add(
                SnapshotCount::snapshotCreated.greaterThanOrEqualTo(
                    startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                )
            )
        }
        if (endDate != null) {
            specs.add(
                SnapshotCount::snapshotCreated.lessThan(
                    endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                )
            )
        }
        if (cameraName != null) {
            specs.add(SnapshotCount::snapshotCameraName.equal(cameraName))
        }
        if (location != null) {
            specs.add(SnapshotCount::snapshotCameraLocation.equal(location))
        }

        val pageable = PageRequest.of(
            page, size,
            Sort.by(
                direction, sort.asPropertyName()
            )
        )
        return snapshotCountRepo.findAll(
            and(specs),
            pageable
        ).toCachedWebResponseEntity(appProperties.detectionCacheSeconds, pageable)
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
        countSpecs.add(SnapshotCount::zeroValue.isFalse())

        val pageRequest = PageRequest.of(
            page, size,
            Sort.by(
                listOf(
                    Sort.Order.desc(SnapshotCount::snapshotCreated.name),
                    Sort.Order.asc(SnapshotCount::type.name),
                )
            )
        )
        val counts = snapshotCountRepo.findAll(
            and(countSpecs),
            pageRequest
        )
        if (counts.isNotEmpty()) {
            annotationSpecs.add(
                Annotation::snapshotCreated.greaterThanOrEqualTo(
                    counts.last().snapshotCreated
                )
            )
            annotationSpecs.add(
                Annotation::snapshotCreated.lessThanOrEqualTo(
                    counts.first().snapshotCreated
                )
            )
        }
        val annotationMap = annotationRepo.findAll(and(annotationSpecs)).groupBy {
            Pair(it.snapshotImageId, it.type)
        }

        val list = counts.map {
            DetectionResponse(
                date = LocalDate.ofInstant(it.snapshotCreated, ZoneId.systemDefault()),
                instant = it.snapshotCreated,
                location = it.snapshotCameraLocation,
                cameraName = it.snapshotCameraName,
                type = it.type,
                value = it.value,
                imageSrc = storageService.uri(it.snapshotImageId).toString(),
                annotations = annotationMap[Pair(it.snapshotImageId, it.type)] ?: emptyList(),
                longitude = it.snapshot.camera.longitude.toBigDecimal(),
                latitude = it.snapshot.camera.latitude.toBigDecimal(),
            )
        }
        return PageImpl(list, pageRequest, Long.MAX_VALUE)
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
                    annotations = annotations,
                    longitude = BigDecimal.ONE,
                    latitude = BigDecimal.ONE,
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

    private fun cacheControl() =
        CacheControl.maxAge(appProperties.detectionCacheSeconds, TimeUnit.SECONDS).cachePublic()
}
