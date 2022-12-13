package com.katalisindonesia.banyuwangi.util

import com.katalisindonesia.banyuwangi.controller.WebResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.Optional

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
