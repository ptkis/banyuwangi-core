package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.FcmToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FcmTokenRepo : JpaRepository<FcmToken, UUID> {
    fun findByRegistrationTokenEquals(registrationToken: String): List<FcmToken>
}
