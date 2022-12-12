package com.katalisindonesia.imageserver.service

import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.imageserver.controller.ImageProperties
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.io.File
import java.net.URI
import java.util.UUID

@Service
class StorageService(
    private val imageProperties: ImageProperties,
    private val appProperties: AppProperties,
) {
    fun file(id: UUID): File {
        return File("${imageProperties.folder}/$id.jpg")
    }

    fun dummyId() = imageProperties.dummyId

    fun dummyResource(): Resource = ClassPathResource("/dog_bike_car.jpg")

    fun store(bytes: ByteArray): UUID {
        val uuid = UUID.randomUUID()
        File(imageProperties.folder).mkdirs()

        File("${imageProperties.folder}/$uuid.jpg").writeBytes(bytes)

        return uuid
    }

    fun uri(uuid: UUID): URI =
        UriComponentsBuilder.fromUri(appProperties.baseUri).path("/v1/image/$uuid").build().toUri()
}
