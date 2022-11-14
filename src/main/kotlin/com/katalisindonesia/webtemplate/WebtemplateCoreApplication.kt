package com.katalisindonesia.webtemplate

import com.katalisindonesia.webtemplate.controller.HelloController
import com.katalisindonesia.webtemplate.security.CorsProperties
import com.katalisindonesia.webtemplate.security.SecurityConfig
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
