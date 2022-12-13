package com.katalisindonesia.banyuwangi.streaming

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @POST("9f8c835e-1896-4f54-b544-29d2a6df802a/{host}/{port}/{channel}/html/{html}/web_port/{webPort}")
    fun getCameraUrl(
        @Header("Content-type") contentType: String,
        @Path("host") host: String,
        @Path("port") port: Int,
        @Path("webPort") webPort: Int,
        @Path("channel") channel: Int,
        @Path("html") html: Boolean,
        @Query("user") user: String,
        @Query("password") password: String,
        @Query("type") type: String,
    ): Call<String>
/*

   fun get
*/
}
