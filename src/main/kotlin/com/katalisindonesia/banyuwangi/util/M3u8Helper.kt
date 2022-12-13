package com.katalisindonesia.banyuwangi.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//                                    year     month     day       hour      minute
private val dtPattern = Regex("2[0-9]{3}[0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9]")
private val dtFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmm")

fun extractGreatestDateTimeM3u8(m3u8Content: String): LocalDateTime? {
    var greatest: String? = null

    dtPattern.findAll(m3u8Content).forEach {
        val str = it.value
        val greatest1 = greatest
        if (greatest1 == null || greatest1 < str) {
            greatest = str
        }
    }
    if (greatest == null) {
        return null
    }
    return LocalDateTime.from(dtFormat.parse(greatest))
}
