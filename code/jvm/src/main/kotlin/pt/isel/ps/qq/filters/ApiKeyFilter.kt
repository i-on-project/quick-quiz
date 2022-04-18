package pt.isel.ps.qq.filters

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PatchMapping
import pt.isel.ps.qq.data.dto.ErrorDto
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.websocket.server.PathParam

@Component
@Order(0)
class ApiKeyFilter(
    private val mapper: ObjectMapper
): HttpFilter() {

    companion object {
        const val API_KEY_NOT_AVAILABLE = "Bad api-key"
    }

    override fun doFilter(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?) {
        println("I was in API Filter")
        if(!validateApiKey(request?.getHeader("API-KEY"))) {
            val code = HttpStatus.FORBIDDEN.value()
            response?.status = code
            val str = mapper.writeValueAsString(ErrorDto(code = code, reason = API_KEY_NOT_AVAILABLE))
            response?.writer?.write(str)
            return
        }

        chain?.doFilter(request, response)
    }

    private fun validateApiKey(key: String?): Boolean = key != null

}