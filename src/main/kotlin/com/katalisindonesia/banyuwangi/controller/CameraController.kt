package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Optional
import java.util.UUID
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
        return WebResponse(success = true, message = "ok", data = cameraRepo.findAll(
            PageRequest.of(page, size, Sort.by(Camera::location.name, Camera::name.name))))
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('camera:write')")
    fun edit(@RequestBody @Valid camera: Camera): ResponseEntity<WebResponse<Camera>> {
        cameraRepo.saveAndFlush(camera)
        return Optional.of(camera).toResponseEntity()
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

fun <T> Optional<T>.toResponseEntity(): ResponseEntity<WebResponse<T>>{
    if (this.isEmpty) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(WebResponse(
            success = false,
            message = "not found",
            data = null,
        ))
    }
    return ResponseEntity.ok(WebResponse(
        success = true,
        message = "ok",
        data = this.get()
    ))
}
