package com.katalisindonesia.banyuwangi

import com.katalisindonesia.banyuwangi.model.Total
import kotlin.reflect.KMutableProperty1

enum class TotalPreferredProperty(
    val property: KMutableProperty1<Total, Long>
) {
    COUNT_ALARM(Total::countAlarmValue),
    COUNT(Total::countValue),
    SUM(Total::sumValue),
    AVG(Total::avgValue),
    MAX(Total::maxValue),
    ;
}
