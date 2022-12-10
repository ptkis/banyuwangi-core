package com.katalisindonesia.imageserver.service

import com.katalisindonesia.imageserver.controller.ImageProperties
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.io.File
import java.util.UUID

@Service
class StorageService(
    private val imageProperties: ImageProperties
) {
    fun file(id: UUID): File {
        return File("${imageProperties.folder}/$id.jpg")
    }

    fun dummyId() = imageProperties.dummyId

    fun dummyResource(): Resource = ClassPathResource("/dog_bike_car.jpg")

    fun store(bytes: ByteArray): UUID {
        val uuid = UUID.randomUUID()

        File("${imageProperties.folder}/$uuid.jpg").writeBytes(bytes)

        return uuid
    }
}
