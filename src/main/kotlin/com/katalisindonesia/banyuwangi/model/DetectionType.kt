package com.katalisindonesia.banyuwangi.model

enum class DetectionType {
    FLOOD,
    TRASH,
    STREETVENDOR,
    CROWD,
    TRAFFIC,
    ;

    fun localizedName(): String {
        return when (this) {
            FLOOD -> "Banjir"
            TRASH -> "Sampah"
            STREETVENDOR -> "PKL"
            CROWD -> "Keramaian"
            TRAFFIC -> "Lalu Lintas"
        }
    }
}
