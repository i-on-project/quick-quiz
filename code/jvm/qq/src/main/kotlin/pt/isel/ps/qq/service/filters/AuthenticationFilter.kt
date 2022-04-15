package pt.isel.ps.qq.service.filters

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import pt.isel.ps.qq.service.data.UserAuthenticationToken
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class UserDatabase{
    fun verifyUser(user: UserAuthenticationToken): Boolean {
        return true
    }
}

class TokenAuthenticationFilter(
    @Autowired private val userDetailsService: UserDetailsService,
    private val userDatabase: UserDatabase
): AbstractAuthenticationProcessingFilter(AntPathRequestMatcher("/api/web/v1.0/guest/login", "POST")) {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val token = UserAuthenticationToken(request.getParameter("token"))
        val userDetails = userDetailsService.loadUserByUsername(token.getUserId())

        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

        return authentication
    }
}