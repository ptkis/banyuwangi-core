package com.katalisindonesia.banyuwangi.model

import javax.persistence.Entity

@Entity
data class DeleteLog(
    var count: Long?,
    var freeSpaceAfter: Long?,
    var freeSpaceBefore: Long?,
    var deletedBytes: Long?,
    var errorMessage: String?,
) : Persistent()
