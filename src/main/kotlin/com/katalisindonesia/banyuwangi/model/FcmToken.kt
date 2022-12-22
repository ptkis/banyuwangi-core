package com.katalisindonesia.banyuwangi.model

import org.hibernate.envers.Audited
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Audited
@Table(
    indexes = [
        Index(name = "fcmtoken_registrationtoken_idx", columnList = "registrationToken")
    ]
)
data class FcmToken(
    var username: String,
    var registrationToken: String,
) : Persistent()
