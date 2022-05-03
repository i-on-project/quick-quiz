package pt.isel.ps.qq.filters

import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Order(0)
@Component
class TestFilter: HttpFilter() {

    companion object {
        val logger = LoggerFactory.getLogger(TestFilter::class.java)
    }

    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        logger.info("${request.method} ${request.requestURI}")
        chain.doFilter(request, response)
    }
}