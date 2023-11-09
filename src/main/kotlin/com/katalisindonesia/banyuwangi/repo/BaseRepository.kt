package com.katalisindonesia.banyuwangi.repo

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

@NoRepositoryBean
interface BaseRepository<T, ID : Serializable?> : JpaRepository<T, ID> {
    fun findAll(spec: Specification<T>, offset: Long, maxResults: Int, sort: Sort): List<T>
    fun findAll(spec: Specification<T>, offset: Long, maxResults: Int): List<T>
    fun findAll(spec: Specification<T>, pageable: Pageable): List<T>
    fun countAll(spec: Specification<T>): Long
}
