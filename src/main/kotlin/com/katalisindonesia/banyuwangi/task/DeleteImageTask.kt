package com.katalisindonesia.banyuwangi.task

import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.DeleteLog
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import com.katalisindonesia.banyuwangi.repo.DeleteLogRepo
import com.katalisindonesia.banyuwangi.repo.SnapshotCountRepo
import com.katalisindonesia.imageserver.service.StorageService
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger { }

@Service
class DeleteImageTask(
    private val snapshotCountRepo: SnapshotCountRepo,
    private val storageService: StorageService,
    private val appProperties: AppProperties,
    private val deleteLogRepo: DeleteLogRepo,

    transactionManager: PlatformTransactionManager,
) {
    private val tt = TransactionTemplate(transactionManager)

    @Scheduled(
        initialDelayString = "\${dashboard.task.deleteimage.fixedDelaySeconds:999999999}",
        fixedDelayString = "\${dashboard.task.deleteimage.fixedDelaySeconds:999999999}",
        timeUnit = TimeUnit.SECONDS,
    )
    fun delete() {
        try {
            val log = doDelete()
            deleteLogRepo.saveAndFlush(log)
        } catch (expected: Exception) {
            deleteLogRepo.saveAndFlush(
                DeleteLog(
                    count = 0,
                    freeSpaceBefore = null,
                    freeSpaceAfter = null,
                    deletedBytes = null,
                    errorMessage = expected.message,
                )
            )
            log.error(expected) {
                "Exception during deletion. Will retry later."
            }
        }
    }

    /**
     * Do actual deletion.
     *
     * @return deleted size in bytes
     */
    internal fun doDelete(minFreeSpace: Long = appProperties.minFreeSpace): DeleteLog {
        val freeSpace = storageService.freeSpace()

        var deleted = 0L
        var countTotal = 0L

        if (minFreeSpace > freeSpace) {
            val diff = minFreeSpace - freeSpace
            val toFree = (diff * 2).coerceAtLeast(diff)

            log.info { "Need to delete $toFree bytes of images due to free space of $freeSpace is below $minFreeSpace" }

            do {

                val page = snapshotCountRepo.findWithIsImageDeleted(
                    isImageDeleted = false,
                    PageRequest.of(0, appProperties.batchSize, Sort.by(SnapshotCount::snapshotCreated.name))
                )

                for (count in page) {
                    deleted += count.snapshot.length

                    if (deleted >= toFree) {
                        break
                    }

                    tt.execute {
                        storageService.delete(count.snapshotImageId)
                        count.isImageDeleted = true
                        snapshotCountRepo.saveAndFlush(count)
                        countTotal++
                    }
                }
            } while (!page.isEmpty && deleted < toFree)
        }
        return DeleteLog(
            count = countTotal,
            freeSpaceBefore = freeSpace,
            freeSpaceAfter = storageService.freeSpace(),
            deletedBytes = deleted,
            errorMessage = null,
        )
    }
}
