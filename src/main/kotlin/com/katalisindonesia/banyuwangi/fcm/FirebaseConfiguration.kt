package com.katalisindonesia.banyuwangi.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.UUID

@Configuration
@EnableConfigurationProperties(FirebaseProperties::class)
class FirebaseConfiguration(val firebaseProperties: FirebaseProperties) {

    @Bean
    fun googleCredentials(): GoogleCredentials {
        if (firebaseProperties.serviceAccount != null) {
            firebaseProperties.serviceAccount?.inputStream.use { stream ->
                return GoogleCredentials.fromStream(
                    stream
                )
            }
        }
        // Use standard credentials chain. Useful when running inside GKE
        return GoogleCredentials.getApplicationDefault()
    }

    @Bean
    fun firebaseApp(credentials: GoogleCredentials?): FirebaseApp {
        val options: FirebaseOptions = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()
        return FirebaseApp.initializeApp(options, UUID.randomUUID().toString())
    }

    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }
}
