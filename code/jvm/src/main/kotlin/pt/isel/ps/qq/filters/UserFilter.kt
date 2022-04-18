package pt.isel.ps.qq.filters

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.dto.ErrorDto
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Order(1)
class UserFilter(   private val mapper: ObjectMapper
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
            val str = mapper.writeValueAsString(ErrorDto(code = code, reason = USER_TOKEN_EXPIRED))
            response?.writer?.write(str)
            return
        }

        chain?.doFilter(request, response)
    }

    private fun validateAuthorization(auth: String?): Boolean {
        //TODO: Need to get info from DB

        return true;
    }

}


