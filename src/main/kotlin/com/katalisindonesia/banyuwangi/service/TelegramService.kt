package com.katalisindonesia.banyuwangi.service

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.logging.LogLevel
import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.TelegramChat
import com.katalisindonesia.banyuwangi.repo.TelegramChatRepo
import mu.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

private val log = KotlinLogging.logger {  }

@Service
class TelegramService (
   private val appProperties: AppProperties,
    private val telegramChatRepo: TelegramChatRepo,
){

    private val bot = bot {
        token = appProperties.telegramToken
        timeout = 30
        logLevel = LogLevel.Network.Body

        dispatch {
            command("start") {
                bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = """/start Berlangganan peringatan
                    |/stop Stop peringatan
                """.trimMargin())
            }

            telegramError {
                log.error { error.getErrorMessage() }
            }
        }
    }

    fun start(chatId: ChatId.Id) {
        val chat = TelegramChat(chatId.id)
        try {
            telegramChatRepo.saveAndFlush(chat)
            bot.sendMessage(chatId = chatId, text = "Berlangganan peringatan")
        } catch (e: DataIntegrityViolationException) {
            log.debug(e) {"Duplicate subscription: "+chatId.id}
            bot.sendMessage(chatId = chatId, text = "Anda sudah berlangganan")
        }
    }

    fun stop(chatId: ChatId.Id) {
        telegramChatRepo.deleteByChatId(chatId.id)
        bot.sendMessage(chatId = chatId, text = "Berhenti berlangganan peringatan")
    }


    @PostConstruct
    fun init() {
        bot.startPolling()
    }
}
