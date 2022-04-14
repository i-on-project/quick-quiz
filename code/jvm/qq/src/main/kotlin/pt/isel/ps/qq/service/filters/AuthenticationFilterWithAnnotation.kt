package pt.isel.ps.qq.service.filters

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import pt.isel.ps.qq.service.annotations.RequestAuthorization
import pt.isel.ps.qq.service.data.UserAuthenticationToken
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationFilterWithAnnotation(@Autowired routes: RequestMappingHandlerMapping): HttpFilter() {

    private val routes: List<RequestMappingInfo>

    init {
        val map = routes.handlerMethods.filter { it.value.hasMethodAnnotation(RequestAuthorization::class.java) }
        this.routes = map.keys.toList()
    }

    override fun doFilter(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?) {
        if(!verifyIfNeedsAuthorization(request)) chain?.doFilter(request, response)
        val token = request?.getHeader("Authorization")
        if(token == null) {
            response?.status = HttpStatus.FORBIDDEN.value()
            return
        }
        if(!validateUserAuthenticationToken(UserAuthenticationToken(token = token))) {
            response?.status = HttpStatus.FORBIDDEN.value()
            return
        }
        chain?.doFilter(request, response)
    }

    private fun verifyIfNeedsAuthorization(request: HttpServletRequest?): Boolean {
        return true
        //val list = routes.find {
        //    if(!it.methodsCondition.methods.contains(request?.method?.let { it1 -> RequestMethod.valueOf(it1) })) return false
        //    it.
        //return list.isNotEmpty()
    }

    private fun validateUserAuthenticationToken(user: UserAuthenticationToken): Boolean {
        return true
    }
}