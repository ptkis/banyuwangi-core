package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import com.katalisindonesia.banyuwangi.model.Total
import com.katalisindonesia.banyuwangi.repo.TotalRepo
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class TotalConsumer(
    private val totalRepo: TotalRepo,
    private val appProperties: AppProperties,
) {
    @RabbitListener(
        queues = [
            "#{totalQueue.name}"
        ]
    )
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Retryable
    fun total(snapshotCounts: List<SnapshotCount>) {
        for (count in snapshotCounts) {
            val chronoUnit = appProperties.totalTruncateChronoUnit
            val instant = count.snapshotCreated.truncatedTo(chronoUnit)
            val total = totalRepo.findByTypeEqualsAndChronoUnitEqualsAndInstantEquals(
                type = count.type,
                chronoUnit = chronoUnit,
                instant = instant,
            ).orElseGet {
                Total(
                    type = count.type,
                    instant = instant,
                    chronoUnit = chronoUnit,
                )
            }

            val maxValue = count.maxValue
            if (maxValue != null && count.value > maxValue) {
                total.countAlarmValue += 1
            }

            if (count.value > 0) {
                total.countValue += 1
            }
            total.maxValue = total.maxValue.coerceAtLeast(count.value.toLong())
            total.sumValue += count.value
            if (total.countValue > 0) {
                total.avgValue = total.sumValue / total.countValue
            }

            totalRepo.saveAndFlush(total)
        }
    }
}
