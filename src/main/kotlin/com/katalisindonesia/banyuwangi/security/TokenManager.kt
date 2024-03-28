package com.katalisindonesia.banyuwangi.security

// @Component
class TokenManager/*(private val authorizedClientManager: OAuth2AuthorizedClientManager)*/ {
    fun accessToken(clientRegistrationName: String, clientId: String): String {
/*        val authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationName)
            .principal(clientId)
            .build()
        val authorizedClient = authorizedClientManager.authorize(authorizeRequest)
        val accessToken = authorizedClient!!.accessToken
        return accessToken.tokenValue*/
        return clientRegistrationName + clientId // dummy
    }
}
