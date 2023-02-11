package com.katalisindonesia.banyuwangi.consumer

data class Dataset(
    val coco: Boolean,
    val streetvendor: Boolean = false,
    val garbage: Boolean = false,
    val flood: Boolean = false,
    val traffic: Boolean = false,
    val crowd: Boolean = false,
)
