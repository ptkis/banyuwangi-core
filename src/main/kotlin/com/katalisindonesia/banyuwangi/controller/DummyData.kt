package com.katalisindonesia.banyuwangi.controller

import com.katalisindonesia.banyuwangi.model.BoundingBox
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.ThreadLocalRandom

val desas = listOf(
    "Kampung Mandar",
    "Kampung Melayu",
    "Karangrejo",
    "Kebalenan",
    "Kepatihan",
    "Kertosari",
    "Lateng",
    "Pakis",
    "Panderejo",
    "Penganjuran",
    "Pengantigan",
    "Singonegaran",
    "Singotrunan",
    "Sobo",
    "Sumber Rejo",
    "Taman Baru",
    "Temenggungan",
    "Tukang Kayu",
)

private const val DEFAULT_DAYS = 30L
private const val DUMMY_RANGE = 5000L
private const val DUMMY_MAX_VALUE = 10000L

fun dummyLabels(startDate: LocalDate?, endDate: LocalDate?): List<ZonedDateTime> {
    val list = mutableListOf<ZonedDateTime>()
    val date: ZonedDateTime = startDate?.atStartOfDay(ZoneId.systemDefault()) ?: ZonedDateTime.now().minusDays(
        DEFAULT_DAYS
    )
    val end = endDate?.atStartOfDay(ZoneId.systemDefault()) ?: ZonedDateTime.now()
    for (i in 0L..DUMMY_RANGE) {
        val current = date.plusHours(i)
        list.add(current)

        if (current > end) {
            break
        }
    }
    return list
}

fun <T> dummyData(seriesNames: List<String>, labels: List<T>): Map<String, List<Long>> {
    val map = mutableMapOf<String, List<Long>>()

    for (name in seriesNames) {
        val list = mutableListOf<Long>()

        for (i in 1..labels.size) {
            list.add((Math.random() * DUMMY_MAX_VALUE).toLong())
        }

        map[name] = list
    }

    return map
}

fun randomInstantBetween(startInclusive: Instant, endExclusive: Instant): Instant {
    val startSeconds: Long = startInclusive.epochSecond
    val endSeconds: Long = endExclusive.epochSecond
    val random: Long = ThreadLocalRandom
        .current()
        .nextLong(startSeconds, endSeconds)
    return Instant.ofEpochSecond(random)
}

/**
 * Dummy annotations.
class: "dog", probability: 0.96922, bounds: [x=0.162, y=0.357, width=0.250, height=0.545]
class: "bicycle", probability: 0.66656, bounds: [x=0.152, y=0.249, width=0.570, height=0.558]
class: "truck", probability: 0.62682, bounds: [x=0.610, y=0.131, width=0.284, height=0.167]
 */
fun detectionAnnotations() = listOf(
    DetectionAnnotation(
        BoundingBox(
            x = 0.162,
            y = 0.357,
            height = 0.545,
            width = 0.250,
        ),
        confidence = 0.96922,
    ),
    DetectionAnnotation(
        BoundingBox(
            x = 0.152,
            y = 0.249,
            height = 0.570,
            width = 0.558,
        ),
        confidence = 0.66656,
    ),
    DetectionAnnotation(
        BoundingBox(
            x = 0.610,
            y = 0.131,
            height = 0.284,
            width = 0.167,
        ),
        confidence = 0.62682,
    ),
)
