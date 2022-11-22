package com.katalisindonesia.banyuwangi.security

import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Optional

/*
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 19/08/18
 * Time: 21.54
 * To change this template use File | Settings | File Templates.
 */
@Service("springSecurityAuditorAware")
class SpringSecurityAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated) {
            return Optional.empty()
        }
        val principal = authentication.principal

        if (principal is String) {
            return Optional.of(principal)
        }
        if (principal is UserDetails) {
            return Optional.of(principal.username)
        }
        return Optional.empty()
    }
}
