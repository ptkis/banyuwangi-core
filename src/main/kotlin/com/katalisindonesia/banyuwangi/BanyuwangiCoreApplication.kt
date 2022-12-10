package com.katalisindonesia.banyuwangi

import com.katalisindonesia.banyuwangi.controller.HelloController
import com.katalisindonesia.banyuwangi.model.Persistent
import com.katalisindonesia.banyuwangi.repo.CameraRepo
import com.katalisindonesia.banyuwangi.security.CorsProperties
import com.katalisindonesia.banyuwangi.security.SecurityConfig
import com.katalisindonesia.banyuwangi.security.TokenManager
import com.katalisindonesia.imageserver.controller.ImageController
import com.katalisindonesia.imageserver.controller.ImageProperties
import com.katalisindonesia.imageserver.service.StorageService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackageClasses = [
        HelloController::class,
        SecurityConfig::class,
        TokenManager::class,
        CameraRepo::class,
        StorageService::class,
        ImageController::class,
    ]
)
@EnableConfigurationProperties
@ConfigurationPropertiesScan(
    basePackageClasses = [
        CorsProperties::class,
        ImageProperties::class,
    ]
)
@EntityScan(
    basePackageClasses = [
        Persistent::class,
    ]
)
class BanyuwangiCoreApplication

fun main(args: Array<String>) {
    runApplication<BanyuwangiCoreApplication>(*args)
}
