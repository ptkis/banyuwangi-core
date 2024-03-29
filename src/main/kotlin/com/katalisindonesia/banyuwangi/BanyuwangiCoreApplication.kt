package com.katalisindonesia.banyuwangi

import com.katalisindonesia.banyuwangi.consumer.MessagingConfig
import com.katalisindonesia.banyuwangi.consumer.MessagingProperties
import com.katalisindonesia.banyuwangi.controller.HelloController
import com.katalisindonesia.banyuwangi.fcm.FirebaseConfiguration
import com.katalisindonesia.banyuwangi.model.Persistent
import com.katalisindonesia.banyuwangi.repo.BaseRepositoryImpl
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import com.katalisindonesia.banyuwangi.security.CorsProperties
import com.katalisindonesia.banyuwangi.security.SecurityConfig
import com.katalisindonesia.banyuwangi.service.CaptureService
import com.katalisindonesia.banyuwangi.streaming.StreamingRest
import com.katalisindonesia.banyuwangi.task.CaptureTask
import com.katalisindonesia.imageserver.controller.ImageController
import com.katalisindonesia.imageserver.controller.ImageProperties
import com.katalisindonesia.imageserver.service.StorageService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(
    scanBasePackageClasses = [
        HelloController::class,
        SecurityConfig::class,
        // TokenManager::class,
        CameraRepo::class,
        StorageService::class,
        ImageController::class,
        CaptureTask::class,
        MessagingConfig::class,
        CaptureService::class,
        StreamingRest::class,
        FirebaseConfiguration::class,
    ]
)
@EnableScheduling
@EnableRetry
@EnableConfigurationProperties
@ConfigurationPropertiesScan(
    basePackageClasses = [
        CorsProperties::class,
        ImageProperties::class,
        MessagingProperties::class,
        AppProperties::class,
        StreamingProperties::class,
    ]
)
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl::class)
@EntityScan(
    basePackageClasses = [
        Persistent::class,
    ]
)
class BanyuwangiCoreApplication

fun main(args: Array<String>) {
    runApplication<BanyuwangiCoreApplication>(*args)
}
