package pt.isel.ps.qq.filters

import org.springframework.core.annotation.Order
import org.springframework.dao.DataAccessResourceFailureException
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.data.ProblemJson
import pt.isel.ps.qq.exceptions.InvalidCredentialsException
import pt.isel.ps.qq.exceptions.MissingCookieException
import pt.isel.ps.qq.repositories.UserRepository
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Order(1)
class UserFilter(
    private val userRepo: UserRepository, private val scope: UserInfoScope
): HttpFilter() {

    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {

            if(request.cookies == null) throw MissingCookieException()
            val cookie = request.cookies.find { it.name == "Authorization" } ?: throw MissingCookieException() //TODO: 401 required
            val bytes = Base64.getDecoder().decode(cookie.value)
            val auth = String(bytes)
            if(!validateAuthorization(auth)) {
                throw InvalidCredentialsException(auth, )
            }
        } catch(ex: MissingCookieException) {
            val problem = ProblemJson(
                type = "MissingCookieException",
                title = "You are not logged in",
                status = 403,
                instance = request.requestURI,
                values = mapOf("message" to ex.message)
            )
            response.status = problem.status
            response.contentType = ProblemJson.MEDIA_TYPE.toString()
            response.writer?.write(problem.toString())
            return
        } catch(ex: InvalidCredentialsException) {
            val problem = ProblemJson(
                type = "InvalidCredentialsException",
                title = "Your credentials are invalid",
                status = 403,
                instance = request.requestURI,
                values = mapOf("credentials" to ex.credentials, "message" to ex.message)
            )
            response.status = problem.status
            response.contentType = ProblemJson.MEDIA_TYPE.toString()
            response.writer?.write(problem.toString())
            return
        } catch(ex: DataAccessResourceFailureException) {
            val problem = ProblemJson(
                type = "DataAccessResourceFailureException",
                title = "One of the services is currently unavailable",
                status = 502,
                instance = request.requestURI,
                values = mapOf("message" to ex.message)
            )
            response.status = problem.status
            response.contentType = ProblemJson.MEDIA_TYPE.toString()
            response.writer?.write(problem.toString())
            return
        } catch(ex: Exception) {
            val problem = ProblemJson(
                type = "Exception",
                title = "Unknown error",
                status = 500,
                instance = request.requestURI,
                values = mapOf("message" to ex.message)
            )
            response.status = problem.status
            response.contentType = ProblemJson.MEDIA_TYPE.toString()
            response.writer?.write(problem.toString())
            return
        }
        chain.doFilter(request, response)
    }

    private fun validateAuthorization(auth: String?): Boolean {
        if(auth == null) return false
        val userAndToken = auth.split(',')
        val user = userRepo.findById(userAndToken[0]).get()
        if(userAndToken[1] != user.loginToken) return false
        scope.setUser(user)
        return true
    }
}


