package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Optional
import java.util.UUID
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@RequestMapping("/v1/camera")
class CameraController(
    private val cameraRepo: CameraRepo,
) {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('camera:read', 'camera:write')")
    fun list(
        @Valid
        @Min(0)
        @RequestParam(required = false, defaultValue = "0") page: Int,

        @Valid
        @Min(0)
        @RequestParam(required = false, defaultValue = "1000") size: Int,
    ): WebResponse<Page<Camera>> {
        return WebResponse(
            success = true, message = "ok",
            data = cameraRepo.findAll(
                PageRequest.of(page, size, Sort.by(Camera::location.name, Camera::name.name))
            )
        )
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasAnyAuthority('camera:write')")
    fun edit(@RequestBody @Valid camera: Camera): ResponseEntity<WebResponse<Camera>> {
        val existing = cameraRepo.findById(camera.id)

        if (existing.isPresent) {
            val camera1 = existing.get()
            camera.version = camera1.version
            camera.interior = camera1.interior?.copy()
        }
        cameraRepo.saveAndFlush(camera)
        return Optional.of(camera).toResponseEntity()
    }

    @PostMapping("/bulk")
    @Transactional
    @PreAuthorize("hasAnyAuthority('camera:write')")
    fun bulkEdit(@RequestBody @Valid cameras: List<Camera>): ResponseEntity<WebResponse<List<Camera>>> {
        for (camera in cameras) {
            val existing = cameraRepo.findById(camera.id)

            if (existing.isPresent) {
                val camera1 = existing.get()
                camera.version = camera1.version
                camera.interior = camera1.interior?.copy()
            }
        }
        cameraRepo.saveAllAndFlush(cameras)
        return Optional.of(cameras).toResponseEntity()
    }

    @Operation(
        summary = "Delete cameras with specified uuids",
        description = "Delete camera with specified uuids. Ignore non existing camera.",
        security = [
            SecurityRequirement(
                name = "oauth2",
                scopes = ["camera:write"],
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
    @DeleteMapping("/bulk")
    @Transactional
    @PreAuthorize("hasAnyAuthority('camera:write')")
    fun bulkDelete(
        @Parameter(
            description = "list of uuid",
            required = true
        ) @RequestBody @Valid ids: List<UUID>
    ): ResponseEntity<WebResponse<List<UUID>>> {
        for (id in ids) {
            if (cameraRepo.existsById(id)) {
                cameraRepo.deleteById(id)
            }
        }
        cameraRepo.flush()
        return Optional.of(ids).toResponseEntity()
    }

    @GetMapping("/id/{id}")
    fun loadById(@PathVariable id: UUID): ResponseEntity<WebResponse<Camera>> {
        return cameraRepo.findById(id).toResponseEntity()
    }

    @GetMapping("/cameraIndexCode/{id}")
    fun loadByVmsCameraIndexCode(@PathVariable id: String): ResponseEntity<WebResponse<Camera>> {
        return cameraRepo.getCameraByVmsCameraIndexCode(id).toResponseEntity()
    }
}

fun <T> Optional<T>.toResponseEntity(): ResponseEntity<WebResponse<T>> {
    if (this.isEmpty) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            WebResponse(
                success = false,
                message = "not found",
                data = null,
            )
        )
    }
    return ResponseEntity.ok(
        WebResponse(
            success = true,
            message = "ok",
            data = this.get()
        )
    )
}
