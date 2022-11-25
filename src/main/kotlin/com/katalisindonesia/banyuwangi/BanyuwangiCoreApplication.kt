package com.katalisindonesia.banyuwangi

import com.katalisindonesia.banyuwangi.controller.HelloController
import com.katalisindonesia.banyuwangi.security.CorsProperties
import com.katalisindonesia.banyuwangi.security.SecurityConfig
import com.katalisindonesia.banyuwangi.service.TokenManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackageClasses = [
        HelloController::class,
        SecurityConfig::class,
        TokenManager::class,
    ]
)
@EnableConfigurationProperties
@ConfigurationPropertiesScan(
    basePackageClasses = [
        CorsProperties::class,
    ]
)
class BanyuwangiCoreApplication

fun main(args: Array<String>) {
    runApplication<BanyuwangiCoreApplication>(*args)
}
