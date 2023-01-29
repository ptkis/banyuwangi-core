package com.katalisindonesia.banyuwangi.repo

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import java.io.Serializable
import javax.persistence.EntityManager

class BaseRepositoryImpl<T, ID : Serializable?> : SimpleJpaRepository<T, ID>, BaseRepository<T, ID> {
    private val entityManager: EntityManager

    constructor(domainClass: Class<T>?, entityManager: EntityManager) : super(domainClass!!, entityManager) {
        this.entityManager = entityManager
    }

    constructor(entityInformation: JpaEntityInformation<T, *>?, entityManager: EntityManager) : super(
        entityInformation!!, entityManager
    ) {
        this.entityManager = entityManager
    }

    override fun findAll(spec: Specification<T>, offset: Long, maxResults: Int): List<T> {
        return findAll(spec, offset, maxResults, Sort.unsorted())
    }

    override fun findAll(spec: Specification<T>, pageable: Pageable): List<T> {
        return findAll(spec, pageable.offset, pageable.pageSize, pageable.sort)
    }

    override fun findAll(spec: Specification<T>, offset: Long, maxResults: Int, sort: Sort): List<T> {
        val query = getQuery(spec, sort)
        require(offset >= 0) { "Offset must not be less than zero!" }
        require(maxResults >= 1) { "Max results must not be less than one!" }
        query.firstResult = offset.toInt()
        query.maxResults = maxResults
        return query.resultList
    }
}
