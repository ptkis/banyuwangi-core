package com.katalisindonesia.banyuwangi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

@MappedSuperclass
open class Persistent {
    @Id
    @Column(columnDefinition = "binary(16)")
    var id: UUID = UUID.randomUUID()

    @Version
    var version: Long? = null

    @CreatedDate
    @Column(nullable = false)
    @JsonIgnore
    var created: Instant = Instant.now()

    @LastModifiedDate
    @Column(nullable = false)
    @JsonIgnore
    var updated: Instant = Instant.now()

    @CreatedBy
    // length is disabled because we will need it for first user creation
//        @Length(min = 1)
    @JsonIgnore
    var createdBy: String? = ""

    @LastModifiedBy
    @JsonIgnore
    var modifiedBy: String? = ""
}

