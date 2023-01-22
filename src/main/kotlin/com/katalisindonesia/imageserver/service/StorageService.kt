package com.katalisindonesia.imageserver.service

import com.katalisindonesia.banyuwangi.AppProperties
import com.katalisindonesia.imageserver.controller.ImageProperties
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.util.UUID
import kotlin.io.path.Path

@Service
class StorageService(
    private val imageProperties: ImageProperties,
    private val appProperties: AppProperties,
) {
    fun file(id: UUID): File {
        val tryFile = File(pathName(id))

        return if (tryFile.exists()) tryFile else File(legacyPathName(id))
    }

    private fun legacyPathName(id: UUID) = "${imageProperties.folder}/$id.jpg"

    private fun pathName(id: UUID): String {
        val idStr = id.toString()
        val firstFolder = idStr.substring(0..1)
        val secondFolder = idStr.substring(2..3)

        return "${imageProperties.folder}/$firstFolder/$secondFolder/$id.jpg"
    }

    fun dummyId() = imageProperties.dummyId

    fun dummyResource(): Resource = ClassPathResource("/dog_bike_car.jpg")

    fun store(bytes: ByteArray): UUID {
        val uuid = UUID.randomUUID()
        File(imageProperties.folder).mkdirs()

        val file = File(pathName(uuid))
        file.parentFile?.mkdirs()
        file.writeBytes(bytes)

        return uuid
    }

    fun delete(uuid: UUID): Boolean {
        return Files.deleteIfExists(Path(pathName(uuid))) || Files.deleteIfExists(Path(legacyPathName(uuid)))
    }

    fun uri(uuid: UUID): URI =
        UriComponentsBuilder.fromUri(appProperties.baseUri).path("/v1/image/$uuid").build().toUri()

    /**
     * Get free space in image folder.
     * @return bytes
     */
    fun freeSpace(): Long {
        return File(imageProperties.folder).freeSpace
    }
}
