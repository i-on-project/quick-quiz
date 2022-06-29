package pt.isel.ps.qq.controllers.authcontrollers

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.data.RequestLoginOutputModel
import pt.isel.ps.qq.data.SirenModel
import pt.isel.ps.qq.service.AuthenticationService
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.controllers.CookieHandler
import pt.isel.ps.qq.controllers.ExceptionsResponseHandler
import pt.isel.ps.qq.controllers.responsebuilders.UserResponseBuilder
import javax.servlet.http.HttpServletRequest

@RestController("AuthUserController")
class AuthUserController(private val authService: AuthenticationService,
                         private val scope: UserInfoScope,
                         private val cookie: CookieHandler,
                         private val responseBuilder: UserResponseBuilder
)
    : AuthMainController() {

    @PostMapping(Uris.API.Web.V1_0.Auth.Logout.ENDPOINT)
    fun logout(request: HttpServletRequest): ResponseEntity<Any> {
        val expectedCookie = request.cookies.find { it.name == "Authorization" }
        authService.logout(scope.getUser().userName)
        val headers = HttpHeaders()
        if(expectedCookie != null)
            headers.add("Set-Cookie", cookie.expireCookie(expectedCookie))
        return ResponseEntity.ok().headers(headers).build()
    }

    @GetMapping(Uris.API.Web.V1_0.Auth.User.CheckUser.ENDPOINT)
    fun checkUserLoginStatus(request: HttpServletRequest): ResponseEntity<Any> {
        val cookie = request.cookies.find { it.name == "Authorization" }!! //TODO: send problem+json
        val user = authService.checkUserLoginStatus(scope.getUser().userName, scope.getUser().loginToken!!)
        val body = responseBuilder.checkAuthStatus(user)
        return ResponseEntity.ok().body(body)
    }
}