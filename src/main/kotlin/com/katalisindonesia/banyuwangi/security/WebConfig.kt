package com.katalisindonesia.banyuwangi.security

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebConfig {
    @Bean
    fun keycloakConfigResolver(): KeycloakSpringBootConfigResolver {
        return KeycloakSpringBootConfigResolver()
    }

/*
    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientService: OAuth2AuthorizedClientService,
    ): OAuth2AuthorizedClientManager {
        val authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials {
//                val client = DefaultClientCredentialsTokenResponseClient()
//                client.setRestOperations(restTemplate)
//                client.setRequestEntityConverter(OAuth2ClientCredentialsGrantRequestEntityConverter())
//                it.accessTokenResponseClient(client)
            }
            .refreshToken()
            .authorizationCode()
            .build()
        val authorizedClientManager = AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            authorizedClientService
        )
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)
        return authorizedClientManager
    }
*/

/*
    @Bean
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate(HttpComponentsClientHttpRequestFactory())
        val converters = restTemplate.messageConverters
        converters.add(FormHttpMessageConverter())
        converters.add(OAuth2AccessTokenResponseHttpMessageConverter())
        restTemplate.messageConverters = converters
        return restTemplate
    }
*/
}
