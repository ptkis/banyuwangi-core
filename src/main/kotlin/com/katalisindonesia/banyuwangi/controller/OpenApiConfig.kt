package com.katalisindonesia.banyuwangi.controller

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.apache.commons.text.WordUtils
import org.springdoc.core.customizers.OpenApiCustomiser
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig(
    @Value("\${keycloak.auth-server-url}") private val keycloakBaseUrl: String,
    @Value("\${keycloak.realm}") private val keycloakRealm: String,
) {
    private val securitySchemeName = "oauth2"
    private fun createAPIKeyScheme(): SecurityScheme {
        val oauthFlow =
            OAuthFlows()
                .authorizationCode(
                    OAuthFlow()
                        .authorizationUrl("$keycloakBaseUrl/realms/$keycloakRealm/protocol/openid-connect/auth")
                        .tokenUrl("$keycloakBaseUrl/realms/$keycloakRealm/protocol/openid-connect/token")
                )

        return SecurityScheme()
            .type(SecurityScheme.Type.OAUTH2)
            .bearerFormat("JWT")
            .flows(oauthFlow)
            .scheme("bearer")
            .`in`(SecurityScheme.In.HEADER)
            .name("Authorization")
    }

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName))
            .components(Components().addSecuritySchemes(securitySchemeName, createAPIKeyScheme()))
            .servers(listOf(Server().url("/").description("Local server")))
            .info(
                Info()
                    .title("Dasbor AI")
                    .description("OpenAPI")
                    .version(System.getenv("VERSION") ?: "1.0-SNAPSHOT"),
            )
    }

    @Bean
    fun customizeOperations(): OpenApiCustomiser {
        return OpenApiCustomiser { openApi: OpenAPI ->
            openApi.paths.forEach { pathEntry: Map.Entry<String, PathItem> ->
                val pathItem = pathEntry.value
                pathItem.readOperationsMap().forEach { entry ->
                    val httpMethod = entry.key.toString().lowercase()
                    val operation = entry.value
                    val path = pathEntry.key

                    val subpaths =
                        path
                            .split("/")
                            .filter { it != "v1" }
                            .map { toMap -> toMap.trim { !it.isLetterOrDigit() } }
                    val newOperationId =
                        "$httpMethod-${
                        subpaths.joinToString("") {
                            it.lowercase()
                        }
                        }"
                    operation.operationId = newOperationId

                    val newSummary =
                        "${WordUtils.capitalize(httpMethod)}_${subpaths.joinToString("") { WordUtils.capitalize(it) }}"
                    operation.summary = newSummary

                    val securityRequirement =
                        SecurityRequirement()
                            .addList(securitySchemeName, listOf())
                            .addList("authorization-code", listOf())

                    operation.security(listOf(securityRequirement))
                }
            }
        }
    }
}
