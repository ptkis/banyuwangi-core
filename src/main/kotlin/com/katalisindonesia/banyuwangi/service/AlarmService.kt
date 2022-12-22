package com.katalisindonesia.banyuwangi.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.banyuwangi.model.Alarm
import com.katalisindonesia.banyuwangi.model.FcmToken
import com.katalisindonesia.imageserver.service.StorageService
import org.springframework.stereotype.Service

@Service
class AlarmService(
    private val firebaseMessaging: FirebaseMessaging,
    private val appProperties: AppProperties,
    private val mapper: ObjectMapper,
    private val storageService: StorageService,
) {
    fun subscribe(tokens: List<FcmToken>) {
        firebaseMessaging.subscribeToTopic(tokens.map { it.registrationToken }, appProperties.alarmTopic)
    }

    fun unsubscribe(tokens: List<FcmToken>) {
        firebaseMessaging.unsubscribeFromTopic(tokens.map { it.registrationToken }, appProperties.alarmTopic)
    }

    fun sendAlarm(alarm: Alarm) {
        firebaseMessaging.send(
            Message.builder()
                .setTopic(appProperties.alarmTopic)
                .putData("alarm", mapper.writeValueAsString(alarm))
                .putData(
                    "message",
                    "${alarm.snapshotCount.type.localizedName()} di ${alarm.snapshotCount.snapshotCameraName}"
                )
                .putData("messageDetail", "Nilai ${alarm.snapshotCount.value} di atas ambang ${alarm.maxValue}")
                .putData("imageSrc", storageService.uri(alarm.snapshotCount.snapshotImageId).toString())
                .build()
        )
    }
}
