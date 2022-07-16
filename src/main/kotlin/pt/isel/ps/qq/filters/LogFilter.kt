package pt.isel.ps.qq.filters

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import pt.isel.ps.qq.utils.Uris
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Order(0)
@Component
class LogFilter: HttpFilter() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(LogFilter::class.java)
    }

    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        logger.info("${request.method} ${request.requestURI}")
        chain.doFilter(request, response)
    }
}