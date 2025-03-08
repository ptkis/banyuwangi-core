package com.katalisindonesia.banyuwangi.service

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.logging.LogLevel
import com.google.common.util.concurrent.RateLimiter
import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.Alarm
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.TelegramChat
import com.katalisindonesia.banyuwangi.repo.TelegramChatRepo
import com.katalisindonesia.imageserver.service.StorageService
import mu.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service
import java.text.MessageFormat
import javax.annotation.PostConstruct

private val log = KotlinLogging.logger { }

@Service
class TelegramService(
    private val appProperties: AppProperties,
    private val telegramChatRepo: TelegramChatRepo,
    private val storageService: StorageService,
) {
    private val rateLimit = RateLimiter.create(appProperties.telegramRateLimit)

    private val rt = RetryTemplate.builder().maxAttempts(10).exponentialBackoff(100L, 1.2, 10000L).build()

    private val bot = bot {
        token = appProperties.telegramToken
        timeout = 30
        logLevel = LogLevel.Network.Body

        dispatch {
            DetectionType.values().forEach {
                callbackQuery("start${it.name}") {
                    start(ChatId.fromId(update.message!!.chat.id), it)
                }
                callbackQuery("stop${it.name}") {
                    stop(ChatId.fromId(update.message!!.chat.id), it)
                }
            }
            command("start") {
                val list = DetectionType.values().map {
                    listOf(
                        InlineKeyboardButton.CallbackData(
                            text = it.localizedName(),
                            callbackData = "start${it.name}"
                        )
                    )
                }
                val inlineKeyboardMarkup = InlineKeyboardMarkup.create(list.toList())
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "Mau berlangganan peringatan apa?",
                    replyMarkup = inlineKeyboardMarkup,
                )
            }

            command("stop") {
                val list = DetectionType.values().map {
                    listOf(
                        InlineKeyboardButton.CallbackData(
                            text = it.localizedName(),
                            callbackData = "stop${it.name}"
                        )
                    )
                }
                val inlineKeyboardMarkup = InlineKeyboardMarkup.create(list.toList())
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "Mau hentikan peringatan apa?",
                    replyMarkup = inlineKeyboardMarkup,
                )
            }

            telegramError {
                log.error { error.getErrorMessage() }
            }
        }
    }

    fun start(chatId: ChatId.Id, detectionType: DetectionType) {
        val chat = TelegramChat(chatId.id, detectionType)
        try {
            telegramChatRepo.saveAndFlush(chat)
            bot.sendMessage(
                chatId = chatId,
                text = "Anda telah berlangganan peringatan ${detectionType.localizedName()}.\n\n" +
                    "/stop untuk berhenti berlangganan",
            )
        } catch (e: DataIntegrityViolationException) {
            log.debug(e) { "Duplicate subscription: " + chatId.id }
            bot.sendMessage(chatId = chatId, text = "Anda sudah berlangganan")
        }
    }

    fun stop(chatId: ChatId.Id, detectionType: DetectionType) {
        telegramChatRepo.deleteByChatIdAndDetectionType(chatId.id, detectionType)
        bot.sendMessage(
            chatId = chatId,
            text = "Anda telah berhenti berlangganan peringatan ${detectionType.localizedName()}.\n\n" +
                "/start untuk berlangganan peringatan",
        )
    }

    @PostConstruct
    fun init() {
        bot.startPolling()
    }

    fun sendAlarm(alarm: Alarm) {
        val type = alarm.snapshotCount.type

        val titleBody = titleBody(alarm, appProperties)
        val title =
            MessageFormat.format(
                titleBody.title ?: "", type.localizedName(), alarm.snapshotCount.snapshotCameraName
            )
        val body = titleBody.body ?: ""
        val media = TelegramFile.ByFile(storageService.file(alarm.snapshotCount.snapshotImageId))

        val chats = telegramChatRepo.findByDetectionType(type)
        for (chat in chats) {
            rateLimit.acquire()
            rt.execute<Unit, Exception> {
                bot.sendPhoto(
                    ChatId.fromId(chat.chatId), media,
                    caption = "$title\n\n$body\n\n" +
                        "/stop untuk berhenti berlangganan"
                )
            }
        }
    }
}
