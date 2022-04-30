package pt.isel.ps.qq.filters

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.dao.DataAccessResourceFailureException
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.data.ProblemJson
import pt.isel.ps.qq.exceptions.ErrorInstance
import pt.isel.ps.qq.exceptions.IllegalAuthenticationException
import pt.isel.ps.qq.repositories.UserElasticRepository
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Order(1)
class UserFilter(
    private val userRepo: UserElasticRepository, private val scope: UserInfoScope
): HttpFilter() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(UserFilter::class.java)
    }

    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        logger.info("Filtering User...")
        val header: String? = request.getHeader("Authorization")
        try {
            val exception = IllegalAuthenticationException(
                reasonForUser = "Your credentials are invalid or expired.",
                moreDetails = "Your credentials are invalid or expired, please try to login again",
                whereDidTheErrorOccurred = ErrorInstance(method = request.requestURI, instance = header ?: "null")
            )
            if(request.cookies == null) throw exception
            val cookie = request.cookies.find { it.name == "Authorization" } ?: throw exception
            logger.info(cookie.value)

            if(!validateAuthorization(header)) {
                throw throw exception
            }
        } catch(ex: IllegalAuthenticationException) {
            val problem = ProblemJson(ex = ex)
            response.status = problem.status
            response.contentType = ProblemJson.MEDIA_TYPE.toString()
            response.writer?.write(problem.toString())
            return
        } catch(ex: DataAccessResourceFailureException) {
            val problem = ProblemJson(ex = ex, instance = "${request.requestURI}@${header}")
            response.status = problem.status
            response.contentType = ProblemJson.MEDIA_TYPE.toString()
            response.writer?.write(problem.toString())
            return
        }
        logger.info("Filtering User Succeeded")
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


