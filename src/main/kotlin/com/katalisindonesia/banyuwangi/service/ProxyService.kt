package com.katalisindonesia.banyuwangi.service

import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.imageserver.service.ProxyRequest
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger { }

@Service
class ProxyService(
    private val appProperties: AppProperties,
) {

    fun proxy(proxyRequest: ProxyRequest): Optional<ByteArray> {
        val timeoutSeconds = appProperties.timeoutSeconds

        val httpClient = OkHttpClient.Builder()
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .callTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .build()

        log.debug { "Calling ${proxyRequest.uri}" }
        val builder = Request.Builder().url(proxyRequest.uri.toURL())
        for (header in proxyRequest.headers) {
            builder.addHeader(header.key, header.value)
        }
        val req = builder.build()
        val resp = httpClient
            .newCall(req).execute()
        resp.use { resp1: Response ->
            val body = resp1.body
            if (!resp1.isSuccessful) {
                log.debug { "${proxyRequest.uri} is unsuccessful: ${resp1.code}" }
                log.debug { "Body: $body" }
                log.debug { "Headers: ${resp1.headers}" }

                throw ProxyException(message = "HTTP Error ${resp1.code}")
            } else {
                val data = body?.bytes() ?: byteArrayOf()
                return Optional.of(data)
            }
        }
    }
}

class ProxyException(message: String,) : RuntimeException(message)
