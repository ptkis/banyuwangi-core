package com.katalisindonesia.banyuwangi.task

import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import com.katalisindonesia.banyuwangi.repo.SnapshotCountRepo
import com.katalisindonesia.imageserver.service.StorageService
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

private val log = KotlinLogging.logger { }

@Service
class DeleteImageTask(
    private val snapshotCountRepo: SnapshotCountRepo,
    private val storageService: StorageService,
    private val appProperties: AppProperties,

    transactionManager: PlatformTransactionManager,
) {
    private val tt = TransactionTemplate(transactionManager)

    @Scheduled(
        initialDelayString = "\${dashboard.task.deleteimage.fixedDelaySeconds:999999999}",
        fixedDelayString = "\${dashboard.task.deleteimage.fixedDelaySeconds:999999999}"
    )
    fun delete() {
        try {
            doDelete()
        } catch (expected: Exception) {
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
    internal fun doDelete(minFreeSpace: Long = appProperties.minFreeSpace): Long {
        val freeSpace = storageService.freeSpace()

        var deleted = 0L

        if (minFreeSpace > freeSpace) {
            val diff = minFreeSpace - freeSpace
            val toFree = Math.max(diff * 2, diff)

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
                    }
                }
            } while (!page.isEmpty && deleted < toFree)
        }
        return deleted
    }
}
