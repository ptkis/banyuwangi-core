package com.katalisindonesia.banyuwangi.security

import mu.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

private val log = KotlinLogging.logger { }
/*
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 11/08/18
 * Time: 15.08
 * To change this template use File | Settings | File Templates.
 */
@Configuration
class CorsConfig {
    // IMPORTANT: it has to be a normal configuration class,
    // not extending WebMvcConfigurerAdapter or other Spring Security class
    @Bean
    fun customCorsFilter(corsProperties: CorsProperties): FilterRegistrationBean<*> {
        log.info { "Configuring CORS: $corsProperties" }
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        corsProperties.allowedOrigins.split(",").forEach { config.addAllowedOrigin(it) }
        corsProperties.allowedHeaders.split(",").forEach { config.addAllowedHeader(it) }
        corsProperties.allowedMethods.split(",").forEach { config.addAllowedMethod(it) }
        source.registerCorsConfiguration("/**", config)
        val bean = FilterRegistrationBean(CorsFilter(source))
        bean.order = Ordered.HIGHEST_PRECEDENCE
        return bean
    }
}

@ConfigurationProperties(prefix = "cors")
@ConstructorBinding
data class CorsProperties(
    /**
     * Comma separated.
     */
    val allowedOrigins: String = "http://localhost:4200",
    /**
     * Comma separated.
     */
    val allowedHeaders: String = "*",
    /**
     * Comma separated.
     */
    val allowedMethods: String = "*"
)
