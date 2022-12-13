package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.repo.CameraRepo
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/live")
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
        tags = ["chart"],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
            ),
        ]
    )
    @GetMapping("/camera")
    @PreAuthorize("hasAuthority('camera:liveview')")
    fun list(): ResponseEntity<WebResponse<List<LiveCamera>>> {
        return ResponseEntity.ok(
            WebResponse(
                success = true,
                message = "ok",
                data =
                cameraRepo.findWithIsActive(true, Pageable.unpaged())
                    .toList()
                    .map { LiveCamera(it) }
            )
        )
    }
}
