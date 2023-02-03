package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.DeleteLog
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DeleteLogRepo : BaseRepository<DeleteLog, UUID>
