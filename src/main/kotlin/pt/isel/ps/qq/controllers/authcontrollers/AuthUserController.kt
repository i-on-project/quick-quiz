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
import javax.servlet.http.HttpServletRequest

@RestController("AuthUserController")
class AuthUserController(private val authService: AuthenticationService,
                         private val scope: UserInfoScope) : AuthMainController() {

    @PostMapping(Uris.API.Web.V1_0.Auth.Logout.ENDPOINT)
    fun logout(request: HttpServletRequest): ResponseEntity<Any> {
        val cookie = request.cookies.find { it.name == "Authorization" }!!
        val headers = HttpHeaders()
        authService.logout(scope.getUser().userName)
        headers.add("Set-Cookie", expireCookie(cookie))
        return ResponseEntity.ok().headers(headers).build()
    }

    @GetMapping(Uris.API.Web.V1_0.Auth.User.CheckUser.ENDPOINT)
    fun checkUserLoginStatus(request: HttpServletRequest): ResponseEntity<Any> {
        val cookie = request.cookies.find { it.name == "Authorization" }!!
        val doc = authService.checkUserLoginStatus(scope.getUser().userName, scope.getUser().loginToken!!)
        val body = SirenModel(
            clazz = listOf("Login"),
            //properties = Acknowledge.TRUE,
            properties = RequestLoginOutputModel(
                userName = doc.userName,
                displayName = doc.displayName,
            ),
            title = "Welcome ${doc.userName}"
        )
        return ResponseEntity.ok().body(body)
    }
}