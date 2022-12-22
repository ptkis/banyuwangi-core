package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.model.FcmToken
import com.katalisindonesia.banyuwangi.repo.FcmTokenRepo
import com.katalisindonesia.banyuwangi.service.AlarmService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import javax.transaction.Transactional

@RestController
@RequestMapping("/v1/fcm")
class FirebaseController(
    private val fcmTokenRepo: FcmTokenRepo,
    private val alarmService: AlarmService,
) {
    @PutMapping("/device/token/{token}")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    fun subscribe(
        @PathVariable
        token: String,
        userDetails: Principal
    ): ResponseEntity<WebResponse<FcmToken>> {
        val fcmToken = FcmToken(
            username = userDetails.name,
            registrationToken = token
        )
        fcmTokenRepo.saveAndFlush(fcmToken)
        alarmService.subscribe(listOf(fcmToken))

        return ResponseEntity.ok(
            WebResponse(
                success = true,
                message = "ok",
                data = fcmToken,
            )
        )
    }

    @DeleteMapping("/device/token/{token}")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    fun unsubscribe(
        @PathVariable
        token: String,
    ): ResponseEntity<WebResponse<Int>> {
        val fcmTokens = fcmTokenRepo.findByRegistrationTokenEquals(token)
        alarmService.unsubscribe(fcmTokens)
        fcmTokens.forEach {
            fcmTokenRepo.delete(it)
        }
        return ResponseEntity.ok(
            WebResponse(
                success = true,
                message = "ok",
                data = fcmTokens.size,
            )
        )
    }
}
