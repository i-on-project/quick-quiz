package pt.isel.ps.qq.filters

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jsonMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.dto.ErrorDto
import pt.isel.ps.qq.data.dto.input.LoginMeInputModel
import pt.isel.ps.qq.database.UserElasticRepository
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Order(1)
class UserFilter(   private val mapper: ObjectMapper
): HttpFilter() {

    @Autowired
    private val userRepo: UserElasticRepository? = null

    companion object {
        const val USER_TOKEN_EXPIRED = "User Token Expired/User does not exist"
    }

    override fun doFilter(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?) {
        println("I was in User Filter")
        if(!validateAuthorization(request?.getHeader("Authorization"))) {
            val code = HttpStatus.FORBIDDEN.value()
            response?.status = code
            response?.addHeader("Content-Type", "application/json")
            val str = mapper.writeValueAsString(ErrorDto(code = code, reason = USER_TOKEN_EXPIRED))
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


