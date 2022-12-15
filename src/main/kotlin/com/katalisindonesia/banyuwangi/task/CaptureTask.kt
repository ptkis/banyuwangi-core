package com.katalisindonesia.banyuwangi.task

import com.katalisindonesia.banyuwangi.consumer.CaptureRequest
import com.katalisindonesia.banyuwangi.consumer.MessagingProperties
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import mu.KotlinLogging
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger { }

@Service
class CaptureTask(
    private val cameraRepo: CameraRepo,
    private val rabbitTemplate: RabbitTemplate,
    private val messagingProperties: MessagingProperties,
) {
    private val pageSize = 50

    @Scheduled(
        fixedDelayString = "\${dashboard.task.capture.fixedDelaySeconds:999999999}",
        initialDelayString = "\${dashboard.task.capture.fixedDelaySeconds:999999999}",
        timeUnit = TimeUnit.SECONDS
    )
    fun scheduledCapture() {
        doCapture()
    }

    fun doCapture(): Int {
        log.info { "Begin capture task" }
        var page = 0
        var count = 0
        do {
            val cameraPage = cameraRepo.findWithIsActive(active = true, PageRequest.of(page, pageSize))
            if (cameraPage.isEmpty) {
                break
            }
            for (camera in cameraPage) {
                rabbitTemplate.convertAndSend(
                    messagingProperties.captureQueue,
                    CaptureRequest(
                        camera = camera,
                        instant = Instant.now()
                    )
                )
                count++
            }
            page++
        } while (true)
        log.info { "End capture task with $count cameras" }
        return count
    }
}
