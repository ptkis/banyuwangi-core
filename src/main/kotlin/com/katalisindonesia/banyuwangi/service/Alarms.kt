package com.katalisindonesia.banyuwangi.service

import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.Alarm

fun titleBody(alarm: Alarm, appProperties: AppProperties): TitleBody {
    val type = alarm.snapshotCount.type
    val value = alarm.snapshotCount.value
    val minHighValue = appProperties.alarmHighMinimalValues[type]

    if (minHighValue != null && value >= minHighValue) {
        return TitleBody(
            title = appProperties.alarmHighTitles[type] ?: appProperties.alarmTitles[type],
            body = appProperties.alarmHighMessages[type] ?: appProperties.alarmMessages[type],
        )
    }
    return TitleBody(
        title = appProperties.alarmTitles[type],
        body = appProperties.alarmMessages[type],
    )
}
