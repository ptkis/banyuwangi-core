package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.TelegramChat
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
interface TelegramChatRepo : BaseRepository<TelegramChat, UUID> {
    @Transactional
    fun deleteByChatIdAndDetectionType(chatId: Long, detectionType: DetectionType): Int

    fun findByDetectionType(detectionType: DetectionType): List<TelegramChat>
}
