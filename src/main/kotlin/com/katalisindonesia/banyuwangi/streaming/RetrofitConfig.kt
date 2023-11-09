package com.katalisindonesia.banyuwangi.streaming

import com.burgstaller.okhttp.AuthenticationCacheInterceptor
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger { }

private const val DEFAULT_TIMEOUT = 60L
internal object RetrofitConfig {

    private fun getClient(
        baseUrl: String
//        user: String,
//        password: String
    ): Retrofit {
        val interceptor = HttpLoggingInterceptor { message -> log.debug { message } }
        if (log.isDebugEnabled) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.BASIC
        }

        // val authenticator = DigestAuthenticator(Credentials(user, password))

//        val gson = GsonBuilder()
//            .setLenient()
//            .create()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            // .authenticator(CachingAuthenticatorDecorator(authenticator, authCache))
            .addInterceptor(AuthenticationCacheInterceptor(ConcurrentHashMap()))
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            // .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    fun getInstance(
        baseUrl: String
        // user: String,
        // password: String
    ): ApiInterface {
        return getClient(
            baseUrl
            // user,
            // password
        ).create(ApiInterface::class.java)
    }
}
