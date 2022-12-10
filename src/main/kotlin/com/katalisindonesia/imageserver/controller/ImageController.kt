package com.katalisindonesia.imageserver.controller

import com.katalisindonesia.imageserver.service.StorageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.core.io.InputStreamResource
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.validation.Valid

@RestController
@RequestMapping("/v1/image")
class ImageController(private val storageService: StorageService) {

    @Operation(
        summary = "Get image", description = "Get detection image",
        tags = ["image"]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
            ApiResponse(responseCode = "404", description = "Image not found"),
        ]
    )
    @GetMapping(
        "/{id}",
        produces = [
            MediaType.IMAGE_JPEG_VALUE,
        ]
    )
    @PreAuthorize("permitAll()")
    fun load(
        @Parameter(description = "Image id", required = true)
        @Valid
        @PathVariable id: UUID
    ): ResponseEntity<InputStreamResource> {
        if (id == storageService.dummyId()) {
            val res = storageService.dummyResource()
            return ResponseEntity.ok()
                .contentLength(res.contentLength())
                .lastModified(res.lastModified())
                .cacheControl(
                    CacheControl.maxAge(1, TimeUnit.DAYS)
                        .cachePublic()
                )
                .body(InputStreamResource(res.inputStream))
        }

        val file = storageService.file(id)

        if (!file.exists()) {
            return ResponseEntity.notFound()
                .cacheControl(
                    CacheControl.maxAge(1, TimeUnit.DAYS)
                        .cachePublic()
                )
                .build()
        }
        return ResponseEntity.ok()
            .contentLength(file.length())
            .lastModified(file.lastModified())
            .cacheControl(
                CacheControl.maxAge(1, TimeUnit.DAYS)
                    .cachePublic()
            )
            .body(InputStreamResource(file.inputStream()))
    }
}
