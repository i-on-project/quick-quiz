package pt.isel.ps.qq.filters

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import pt.isel.ps.qq.repositories.UserElasticRepository
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Order(1)
class UserFilter(   private val mapper: ObjectMapper, private val userRepo: UserElasticRepository?
): HttpFilter() {

    companion object {
        const val USER_TOKEN_EXPIRED = "User Token Expired/User does not exist"
    }

    override fun doFilter(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?) {
        println("I was in User Filter")
        if(!validateAuthorization(request?.getHeader("Authorization"))) {
            val code = HttpStatus.FORBIDDEN.value()
            response?.status = code
            response?.addHeader("Content-Type", "application/json")
            val str = USER_TOKEN_EXPIRED
            response?.writer?.write(str)
            return
        }

        chain?.doFilter(request, response)
    }

    private fun validateAuthorization(auth: String?): Boolean {
        if(auth == null) return false
        val userAndToken = auth.split(',')
        val user = userRepo?.findById(userAndToken[0])!!.get()
        if(userAndToken[1] != user.loginToken) return false
        return true;
    }

}


