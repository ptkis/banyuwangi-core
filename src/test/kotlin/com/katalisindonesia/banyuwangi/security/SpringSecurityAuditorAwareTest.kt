package com.katalisindonesia.banyuwangi.security

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
internal class SpringSecurityAuditorAwareTest(
    @Autowired
    private val springSecurityAuditorAware: SpringSecurityAuditorAware,
) {
    @Test
    fun `current auditor with no authentication`() {
        val result = springSecurityAuditorAware.currentAuditor

        assertFalse(result.isPresent)
    }
    @Test
    @WithMockUser(username = "admin")
    fun `current auditor with authentication`() {
        val result = springSecurityAuditorAware.currentAuditor

        assertTrue(result.isPresent)
    }
}
