package com.katalisindonesia.banyuwangi.task

import com.katalisindonesia.banyuwangi.consumer.MessagingProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StreamingCheckTask(
    private val rabbitTemplate: RabbitTemplate,
    private val messagingProperties: MessagingProperties,
) {
    @Scheduled(
        fixedDelayString = "\${dashboard.task.streamingCheck.fixedDelaySeconds:999999999}",
        initialDelayString = "\${dashboard.task.streamingCheck.fixedDelaySeconds:999999999}",
        timeUnit = TimeUnit.SECONDS
    )
    fun streamingCheck() {
        rabbitTemplate.convertAndSend(messagingProperties.streamingCheckQueue, "")
    }
}
