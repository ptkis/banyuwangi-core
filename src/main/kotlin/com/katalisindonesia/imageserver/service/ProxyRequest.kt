package com.katalisindonesia.imageserver.service

import java.net.URI

data class ProxyRequest(
    val uri: URI,
    val headers: Map<String, String>,
)
