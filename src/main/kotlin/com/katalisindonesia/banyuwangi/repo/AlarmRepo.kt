package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.Alarm
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AlarmRepo : BaseRepository<Alarm, UUID>
