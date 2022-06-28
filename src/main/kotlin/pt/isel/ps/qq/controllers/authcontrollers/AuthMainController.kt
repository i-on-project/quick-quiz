package pt.isel.ps.qq.controllers.authcontrollers

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.service.DataService
import pt.isel.ps.qq.utils.Uris
import javax.servlet.http.Cookie

@RestController("AuthController")
@RequestMapping(Uris.API.Web.V1_0.Auth.PATH)
class AuthMainController ( ) {
    fun calculateLastPage(total: Long): Int {
        return ((total.toDouble() / DataService.PAGE_SIZE) + 0.5).toInt()
    }

    fun expireCookie(cookie: Cookie): String {
        val builder = StringBuilder("${cookie.name}=;")
        builder.append("Expires=Thu, 01 Jan 1970 00:00:01 GMT;")
        builder.append("Path=/;")
        builder.append("Secure;")
        builder.append("HttpOnly;")
        return builder.toString()
    }


}