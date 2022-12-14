package com.katalisindonesia.banyuwangi.repo

import com.katalisindonesia.banyuwangi.model.Annotation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AnnotationRepo : JpaRepository<Annotation, UUID>
