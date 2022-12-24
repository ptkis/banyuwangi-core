package com.katalisindonesia.banyuwangi.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.AndroidNotification
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.ApnsFcmOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.google.firebase.messaging.WebpushConfig
import com.google.firebase.messaging.WebpushNotification
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
        val title = "${alarm.snapshotCount.type.localizedName()} di ${alarm.snapshotCount.snapshotCameraName}"
        val body = "Nilai ${alarm.snapshotCount.value} di atas ambang ${alarm.maxValue}"
        val imageUrl = storageService.uri(alarm.snapshotCount.snapshotImageId).toString()
        firebaseMessaging.send(
            Message.builder()
                .setAndroidConfig(
                    AndroidConfig.builder()
                        .setNotification(
                            AndroidNotification.builder()
                                .setBody(body)
                                .setTitle(title)
                                .setImage(imageUrl)
                                .setDefaultSound(true)
                                .build()
                        )
                        .setCollapseKey(
                            appProperties.alarmTopic
                        )
                        .build()
                )
                .setApnsConfig(
                    ApnsConfig.builder()
                        .setFcmOptions(
                            ApnsFcmOptions.builder()
                                .setImage(imageUrl)
                                .build()
                        ).build()
                )
                .setWebpushConfig(
                    WebpushConfig.builder()
                        .setNotification(
                            WebpushNotification.builder()
                                .setBody(body)
                                .setTitle(title)
                                .setImage(imageUrl)
                                .build()
                        )
                        .build()
                )
                .setTopic(appProperties.alarmTopic)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .setImage(imageUrl)
                        .build()
                )
                .putData("alarm", mapper.writeValueAsString(alarm))
                .putData(
                    "message",
                    title
                )
                .putData("messageDetail", body)
                .putData("imageSrc", imageUrl)
                .build()
        )
    }
}
