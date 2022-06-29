package pt.isel.ps.qq.controllers

import org.springframework.stereotype.Controller
import javax.servlet.http.Cookie

@Controller
class CookieHandler {
    fun expireCookie(cookie: Cookie): String {
        val builder = StringBuilder("${cookie.name}=;")
        builder.append("Expires=Thu, 01 Jan 1970 00:00:01 GMT;")
        builder.append("Path=/;")
        builder.append("Secure;")
        builder.append("HttpOnly;")
        return builder.toString()
    }

    fun createCookie(cookieName: String, nameValue: String, duration: Long): String {
        return "${cookieName}=${nameValue}; Max-Age=${duration}; Path=/; Secure; HttpOnly; SameSite=Strict"
    }
}