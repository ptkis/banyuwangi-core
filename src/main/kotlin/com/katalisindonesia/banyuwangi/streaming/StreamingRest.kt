package com.katalisindonesia.banyuwangi.streaming

import com.katalisindonesia.banyuwangi.model.CameraType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import retrofit2.Call

@Service
class StreamingRest(
    @Value("\${streaming.server}")
    private val streamingServer: String,
) {

    private val apiService = RetrofitConfig.getInstance(
        baseUrl = streamingServer
//        user = "",
//        password = ""
    )

    fun getCameraUrl(
        user: String,
        password: String,
        host: String,
        port: Int,
        channel: Int,
        html: Boolean,
        type: CameraType,
        webPort: Int,
    ): Call<String> {
        return apiService.getCameraUrl(
            ApiConstant.TYPE_CONTENT_JSON,
            host,
            port,
            webPort = webPort,
            channel,
            html,
            user,
            password,
            type.name,
        )
    }
}
