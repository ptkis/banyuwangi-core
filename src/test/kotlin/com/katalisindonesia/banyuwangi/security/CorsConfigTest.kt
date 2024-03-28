package com.katalisindonesia.banyuwangi.security

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("default", "secret")
internal class CorsConfigTest(
    @Autowired private val corsConfig: CorsConfig,
    @Autowired private val corsProperties: CorsProperties,
) {
    @Test
    fun `try config customCorsFilter`() {
        val filter = corsConfig.customCorsFilter(corsProperties)
        assertNotNull(filter)
    }
}
