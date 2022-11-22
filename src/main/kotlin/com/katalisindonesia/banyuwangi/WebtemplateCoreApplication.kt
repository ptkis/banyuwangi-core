package com.katalisindonesia.banyuwangi

import com.katalisindonesia.banyuwangi.controller.HelloController
import com.katalisindonesia.banyuwangi.security.CorsProperties
import com.katalisindonesia.banyuwangi.security.SecurityConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackageClasses = [
        HelloController::class,
        SecurityConfig::class,
    ]
)
@EnableConfigurationProperties
@ConfigurationPropertiesScan(
    basePackageClasses = [
        CorsProperties::class,
    ]
)
class WebtemplateCoreApplication

fun main(args: Array<String>) {
    runApplication<WebtemplateCoreApplication>(*args)
}
