package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.Alarm
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import com.katalisindonesia.banyuwangi.repo.AlarmRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotCountRepo
import com.katalisindonesia.banyuwangi.service.AlarmService
import com.katalisindonesia.banyuwangi.service.TelegramService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@Service
class TriggerConsumer(
    private val alarmRepo: AlarmRepo,
    private val alarmService: AlarmService,
    private val snapshotCountRepo: SnapshotCountRepo,
    transactionManager: PlatformTransactionManager,
    private val telegramService: TelegramService,

) {
    private val tt = TransactionTemplate(transactionManager)

    @RabbitListener(
        queues = [
            "#{triggerQueue.name}"
        ],
        concurrency = "\${dashboard.messaging.triggerQueue.concurrency}",
    )
    fun analyze(counts: List<SnapshotCount>) {
        for (count in counts) {
            val alarmSetting = count.snapshot.camera.alarmSetting
            if (alarmSetting != null) {
                val max = alarmSetting.max(count.type)
                if (max != null && count.value > max) {
                    val alarm = tt.execute {
                        val alarm1 = Alarm(
                            maxValue = max,
                            snapshotCount = snapshotCountRepo.getReferenceById(count.id)
                        )

                        alarmRepo.saveAndFlush(alarm1)
                        alarm1
                    }!!
                    alarmService.sendAlarm(alarm)
                    telegramService.sendAlarm(alarm)
                }
            }
        }
    }
}
