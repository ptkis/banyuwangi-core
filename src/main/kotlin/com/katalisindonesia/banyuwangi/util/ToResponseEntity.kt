package com.katalisindonesia.banyuwangi.util

import com.katalisindonesia.banyuwangi.controller.ChartData
import com.katalisindonesia.banyuwangi.controller.WebResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.Optional
import java.util.concurrent.TimeUnit

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

fun <T> ChartData<T>.toCachedResponseEntity(seconds: Long): ResponseEntity<ChartData<T>> = ResponseEntity.ok()
    .cacheControl(CacheControl.maxAge(seconds, TimeUnit.SECONDS).cachePublic())
    .body(this)

fun <T> Page<T>.toCachedWebResponseEntity(seconds: Long): ResponseEntity<WebResponse<Page<T>>> = ResponseEntity.ok()
    .cacheControl(CacheControl.maxAge(seconds, TimeUnit.SECONDS).cachePublic())
    .body(
        WebResponse(
            success = true,
            message = "ok",
            data = this,
        )
    )

fun <T> List<T>.toCachedWebResponseEntity(seconds: Long, pageable: Pageable): ResponseEntity<WebResponse<Page<T>>> =
    ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(seconds, TimeUnit.SECONDS).cachePublic())
        .body(
            WebResponse(
                success = true,
                message = "ok",
                data = PageImpl(this, pageable, Long.MAX_VALUE),
            )
        )
fun <T> List<T>.toCachedListWebResponseEntity(seconds: Long): ResponseEntity<WebResponse<List<T>>> =
    ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(seconds, TimeUnit.SECONDS).cachePublic())
        .body(
            WebResponse(
                success = true,
                message = "ok",
                data = this,
            )
        )
