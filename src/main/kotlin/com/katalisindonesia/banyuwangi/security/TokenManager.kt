package com.katalisindonesia.banyuwangi.security

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.stereotype.Component

@Component
class TokenManager(private val authorizedClientManager: OAuth2AuthorizedClientManager) {
    fun accessToken(clientRegistrationName: String, clientId: String): String {
        val authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationName)
            .principal(clientId)
            .build()
        val authorizedClient = authorizedClientManager.authorize(authorizeRequest)
        val accessToken = authorizedClient!!.accessToken
        return accessToken.tokenValue
    }
}
