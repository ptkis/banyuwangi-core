package com.katalisindonesia.banyuwangi.service

import com.github.kotlintelegrambot.entities.ChatId
import com.katalisindonesia.banyuwangi.repo.TelegramChatRepo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles("default", "secret")
class TelegramServiceTest(
    @Autowired
    private val telegramService: TelegramService,
    @Autowired
    private val telegramChatRepo: TelegramChatRepo,
) {
    @Test
    fun test_start_stop() {
        telegramService.start(ChatId.fromId(10))

        assertEquals(1, telegramChatRepo.findAll().size)

        // duplicate
        telegramService.start(ChatId.fromId(10))
        assertEquals(1, telegramChatRepo.findAll().size)

        // different
        telegramService.start(ChatId.fromId(11))
        assertEquals(2, telegramChatRepo.findAll().size)

        // stop
        telegramService.stop(ChatId.fromId(11))
        assertEquals(1, telegramChatRepo.findAll().size)

        // duplicate stop
        telegramService.stop(ChatId.fromId(11))
        assertEquals(1, telegramChatRepo.findAll().size)

        // all stop
        telegramService.stop(ChatId.fromId(10))
        assertEquals(0, telegramChatRepo.findAll().size)
    }

}
