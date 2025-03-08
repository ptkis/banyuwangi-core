package com.katalisindonesia.banyuwangi.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            name = "telegram_chat_chatid_detectiontype_key",
            columnNames = ["chatId", "detectionType"]
        )
    ]
)
class TelegramChat(
    @Column(unique = true, nullable = false)
    val chatId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var detectionType: DetectionType?,
) : Persistent()
