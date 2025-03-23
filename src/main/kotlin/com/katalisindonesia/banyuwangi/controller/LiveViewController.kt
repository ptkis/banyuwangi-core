package com.katalisindonesia.banyuwangi.controller

import au.com.console.jpaspecificationdsl.and
import au.com.console.jpaspecificationdsl.equal
import au.com.console.jpaspecificationdsl.`in`
import au.com.console.jpaspecificationdsl.like
import au.com.console.jpaspecificationdsl.or
import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.constraints.Min

@RestController
@RequestMapping("/v1/live")
@SecurityRequirement(name = "oauth2")
class LiveViewController(
    private val cameraRepo: CameraRepo,
) {
    @Operation(
        summary = "Get camera live view",
        description = "Get all camera live view urls",
        security = [
            SecurityRequirement(
                name = "oauth2",
                scopes = ["camera:liveview"],
            )
        ],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
            ),
        ]
    )
    @GetMapping("/camera", "/camera/list")
    @PreAuthorize("hasAuthority('camera:liveview')")
    fun list(
        @RequestParam(required = false) keyword: String?,

        @Min(0) @RequestParam(required = false, defaultValue = "0") page: Int,
        @Min(1) @RequestParam(required = false, defaultValue = "1000") size: Int,
        @RequestParam(required = false, name = "location") locations: Set<String>?,
        @RequestParam(required = false, name = "id") ids: Set<UUID>?,
        @RequestParam(required = false, name = "type") types: Set<DetectionType>?,
    ): ResponseEntity<WebResponse<List<LiveCamera>>> {
        return ResponseEntity.ok(
            WebResponse(
                success = true,
                message = "ok",
                data =
                cameraRepo.findAll(
                    and(
                        specs(
                            keyword = keyword,
                            locations = locations,
                            ids = ids,
                            types = types,
                        )
                    ),
                    PageRequest.of(
                        page, size, Sort.by(Camera::name.name).ascending()
                    )
                )
                    .map { LiveCamera(it) }
            )
        )
    }

    @GetMapping("/camera/count")
    @PreAuthorize("hasAuthority('camera:liveview')")
    fun count(
        @RequestParam(required = false) keyword: String?,

        @RequestParam(required = false, name = "location") locations: Set<String>?,
        @RequestParam(required = false, name = "id") ids: Set<UUID>?,
        @RequestParam(required = false, name = "type") types: Set<DetectionType>?,

    ): ResponseEntity<WebResponse<Long>> {
        return ResponseEntity.ok(
            WebResponse(
                success = true,
                message = "ok",
                data =
                cameraRepo.countAll(
                    and(
                        specs(
                            keyword = keyword,
                            locations = locations,
                            ids = ids,
                            types = types,
                        )
                    )
                )
            )
        )
    }

    @GetMapping("/location/list")
    @PreAuthorize("hasAuthority('camera:liveview')")
    fun locations(
        @RequestParam(required = false) keyword: String?,
        @Min(0) @RequestParam(required = false, defaultValue = "0") page: Int,
        @Min(1) @RequestParam(required = false, defaultValue = "1000") size: Int,
    ): ResponseEntity<WebResponse<List<String>>> {
        return ResponseEntity.ok(
            WebResponse(
                success = true,
                message = "ok",
                data = cameraRepo.findCameraLocations(
                    keyword = keyword?.wrapWithPercent() ?: "",
                    pageable = Pageable.ofSize(size).withPage(page)
                )
            )
        )
    }

    private fun specs(
        keyword: String?,

        locations: Set<String>?,
        ids: Set<UUID>?,
        types: Set<DetectionType>?,
    ): List<Specification<Camera>> {
        val list = mutableListOf<Specification<Camera>>()
        if (keyword != null) {
            val keywordSpecs = or(
                Camera::name.like(keyword.wrapWithPercent()),
                Camera::location.like(keyword.wrapWithPercent()),
                Camera::vmsCameraIndexCode.like(keyword.wrapWithPercent()),
            )
            list.add(keywordSpecs)
        }
        if (!locations.isNullOrEmpty()) {
            list.add(Camera::location.`in`(locations))
        }

        if (!ids.isNullOrEmpty()) {
            list.add(Camera::id.`in`(ids))
        }
        if (!types.isNullOrEmpty()) {
            val map = mapOf(
                DetectionType.FLOOD to Camera::isFlood,
                DetectionType.CROWD to Camera::isCrowd,
                DetectionType.STREETVENDOR to Camera::isStreetvendor,
                DetectionType.TRASH to Camera::isTrash,
                DetectionType.TRAFFIC to Camera::isTraffic,
            )
            list.addAll(
                types.mapNotNull { map[it] }
                    .map { it.equal(true) }
            )
        }

        list.add(Camera::isActive.equal(true))

        return list
    }
}

fun String.wrapWithPercent(): String = "%$this%"
