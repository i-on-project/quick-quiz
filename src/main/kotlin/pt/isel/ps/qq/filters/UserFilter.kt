package pt.isel.ps.qq.filters

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.data.ProblemJson
import pt.isel.ps.qq.repositories.UserRepository
import pt.isel.ps.qq.service.AuthenticationService
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Order(1)
class UserFilter(
    private val authService: AuthenticationService
) : HttpFilter() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(LogFilter::class.java)
    }

    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {

        LogFilter.logger.info("User Filter: ${request.method} ${request.requestURI}")

        val cookie = request.cookies?.find { it.name == "Authorization" }
        if (cookie == null) {
            missingCookie(request, response)
            return
        }
        val bytes = Base64.getDecoder().decode(cookie.value)
        val auth = String(bytes)
        if (!validateAuthorization(auth)) {
            invalidCredentials(request, response)
            return
        }

        chain.doFilter(request, response)
    }


    private fun validateAuthorization(auth: String?): Boolean {
        if (auth == null) return false
        return authService.validateAuthStatus(auth)
    }

    private fun invalidCredentials(request: HttpServletRequest, response: HttpServletResponse) {
        val problem = ProblemJson(
            type = "InvalidCredentialsException",
            title = "Your credentials are invalid",
            status = 403,
            instance = request.requestURI,
            values = mapOf("message" to "credentials are invalid")
        )
        setErrorResponse(response, problem)
    }

    private fun missingCookie(request: HttpServletRequest, response: HttpServletResponse) {
        val problem = ProblemJson(
            type = "MissingCookieException",
            title = "You are not logged in",
            status = 403,
            instance = request.requestURI,
            values = mapOf("message" to "Authorization cookie is missing")
        )
        setErrorResponse(response, problem)
    }

    private fun setErrorResponse(response: HttpServletResponse, problem: ProblemJson) {
        response.status = problem.status
        response.contentType = ProblemJson.MEDIA_TYPE.toString()
        response.writer?.write(problem.toString())
    }
}


