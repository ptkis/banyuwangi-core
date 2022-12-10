package com.katalisindonesia.banyuwangi.model

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class InstanceAnnotation(
		@Column(nullable = false)
		var name: String,

		@ManyToOne(cascade = [CascadeType.ALL])
		@JoinColumn(nullable = false)
		var boundingBox: BoundingBox,

		@Column(nullable = false)
		var score: Float,

		@Column(nullable = false)
		var analyzed: Boolean = false,

		@Column(nullable = false)
		var crowd: Boolean = false,

		@Column(nullable = false)
		var parking: Boolean = false,

		@Column(nullable = true)
		var traffic: Boolean? = false,

		@Column(nullable = false)
		var litter: Boolean = false,

		@Column(nullable = false)
		var flood: Boolean = false,

		@Column(nullable = false)
		var isMask: Boolean = false,

		@Column(nullable = false)
		var isNoMask: Boolean = false,
) : Persistent()
