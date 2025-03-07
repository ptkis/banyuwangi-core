package com.katalisindonesia.banyuwangi.model

import javax.persistence.Column
import javax.persistence.Entity

@Entity
class TelegramChat(
    @Column(unique = true)
    val chatId: Long,
) : Persistent() {
}
