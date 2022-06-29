package pt.isel.ps.qq.controllers.unauthcontrollers

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.controllers.CookieHandler
import pt.isel.ps.qq.controllers.responsebuilders.UserResponseBuilder
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.service.AuthenticationService
import pt.isel.ps.qq.service.EmailService
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.utils.getBaseUrlHostFromRequest
import java.time.Duration
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController("UserController")
class UserController(private val authenticationService: AuthenticationService,
                     private val emailService: EmailService,
                     private val responseBuilder: UserResponseBuilder,
                     private val cookie: CookieHandler

) : UnauthMainController() {

    /**
     * POST /api/web/v1.0/non_auth/register
     *
     * This handler registers a new user. If the user already exists, the status is still pending and the token is
     * already expired only then we allow the user to register again.
     *
     * Handler to create a new user. After this handler is executed successfully the user will be with the status
     * pending.
     * @param request inject HTTP request
     * @param input values to create the user
     * @return a ResponseEntity with status code 200 and body with a siren response
     *
     * Siren class = Register
     * Siren properties = user values
     * Siren actions = LOGMEIN -> logins this user
     */
    @PostMapping(Uris.API.Web.V1_0.NonAuth.Register.ENDPOINT)
    fun registerUser(request: HttpServletRequest, @RequestBody input: RegisterInputModel): ResponseEntity<Any> {
        val user = authenticationService.register(input)
        val body = responseBuilder.registerUserResponse(user, getBaseUrlHostFromRequest(request))

        if(!user.userName.contains("test")) {
            emailService.sendEmail("$appHost/logmein?user=${user.userName}&token=${user.registrationToken}", user.userName)
        } //TODO: else Return error to contact admin
        return ResponseEntity.ok().body(body)
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.Login.ENDPOINT)
    fun requestLogin(request: HttpServletRequest, @RequestBody userName: LoginInputModel): ResponseEntity<Any> {
        val user = authenticationService.requestLogin(userName)
        val body = responseBuilder.requestLoginResponse(user, getBaseUrlHostFromRequest(request))

        if(!user.userName.contains("test")) {
            emailService.sendEmail("$appHost/logmein?user=${user.userName}&token=${user.requestToken}", user.userName)
        } //TODO: else Return error to contact admin
        return ResponseEntity.ok().body(body)
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.Logmein.ENDPOINT)
    fun loginUser(response: HttpServletResponse, @RequestBody input: LoginMeInputModel): ResponseEntity<Any> {
        val user = authenticationService.logmein(input)
        val str = "${user.userName},${user.loginToken}"
        val base64 = Base64.getEncoder().encodeToString(str.toByteArray())
        val headers = HttpHeaders()
        headers.add("Set-Cookie", cookie.createCookie("Authorization",base64,  Duration.ofDays(7).toSeconds()))
        val body = responseBuilder.loginUserResponse(user)
        return ResponseEntity.ok().headers(headers).body(body)
    }
}