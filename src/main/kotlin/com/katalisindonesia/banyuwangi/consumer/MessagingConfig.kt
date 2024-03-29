package com.katalisindonesia.banyuwangi.consumer

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessagingConfig(
    private val messagingProperties: MessagingProperties,
) {
    @Bean
    fun captureQueue() = Queue(
        messagingProperties.captureQueue,
    )

    @Bean
    fun detectionResultQueue() =
        Queue(
            messagingProperties.detectionResultQueue,
        )

    @Bean
    fun streamingCheckQueue() =
        Queue(
            messagingProperties.streamingCheckQueue,
        )
    @Bean
    fun triggerQueue() =
        Queue(
            messagingProperties.triggerQueue,
        )

    @Bean
    fun totalQueue() =
        Queue(
            messagingProperties.totalQueue,
        )

    @Bean
    fun messageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter(
            jacksonMapperBuilder().addModule(JavaTimeModule()).addModule(Jdk8Module())
                .build()
        )
    }
}
