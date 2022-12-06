package com.katalisindonesia.banyuwangi.controller


data class WebResponse<T>(
    val success: Boolean, val message: String, val data: T?
)
