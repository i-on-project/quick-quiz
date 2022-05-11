package pt.isel.ps.qq.controllers

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.service.AuthenticationService
import pt.isel.ps.qq.service.DataService
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.utils.getBaseUrlHostFromRequest
import java.time.Duration
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(Uris.API.Web.V1_0.NonAuth.PATH)
class UnregisteredController(
    private val authenticationService: AuthenticationService,
    private val dataService: DataService
) {

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
        val body = SirenModel(
            clazz = listOf("Register"),
            properties = RequestLoginOutputModel(username = user.userName, token = user.registrationToken!!, timeout = user.registrationExpireDate!!),
            actions = listOf(
                SirenAction(
                    name = "Logmein",
                    title = "Login",
                    method = SirenSupportedMethods.POST,
                    href = Uris.API.Web.V1_0.NonAuth.Logmein.url(getBaseUrlHostFromRequest(request)),
                    fields = listOf(
                        SirenField(
                            name = "userName",
                            value = user.userName
                        ), SirenField(
                            name = "loginToken",
                            value = user.loginToken
                        )
                    )
            )),
            title = "Check your email"
        )
        return ResponseEntity.ok().body(body)
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.Login.ENDPOINT)
    fun requestLogin(request: HttpServletRequest, @RequestBody userName: LoginInputModel): ResponseEntity<Any> {
        val user = authenticationService.requestLogin(userName)
        val body = SirenModel(
            clazz = listOf("RequestLogin"),
            properties = RequestLoginOutputModel(username = user.userName, token = user.requestToken!!, timeout = user.requestExpireDate!!),
            actions = listOf(
                SirenAction(
                    name = "Logmein",
                    title = "Login",
                    method = SirenSupportedMethods.POST,
                    href = Uris.API.Web.V1_0.NonAuth.Logmein.url(getBaseUrlHostFromRequest(request)),
                    fields = listOf(
                        SirenField(
                            name = "userName",
                            value = user.userName
                        ), SirenField(
                            name = "loginToken",
                            value = user.loginToken
                        )
                    )
            )),
            title = "Check your email"
        )
        return ResponseEntity.ok().body(body)
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.Logmein.ENDPOINT)
    fun loginUser(response: HttpServletResponse, @RequestBody input: LoginMeInputModel): ResponseEntity<Any> {
        val doc = authenticationService.logmein(input)
        val str = "${doc.userName},${doc.loginToken}"
        val base64 = Base64.getEncoder().encodeToString(str.toByteArray())

        val headers = HttpHeaders()
        headers.add("Set-Cookie", "Authorization=$base64; Max-Age=${Duration.ofDays(7).toSeconds()}; Path=/; Secure; HttpOnly;") // SameSite=Strict

        val body = SirenModel(
            clazz = listOf("Login"),
            properties = Acknowledge.TRUE,
            title = "Welcome ${doc.userName}"
        )

        return ResponseEntity.ok().headers(headers).body(body)
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.JoinSession.ENDPOINT)
    fun joinSession(@RequestBody input: JoinSessionInputModel): ResponseEntity<Any> {
        return ResponseEntity.ok().body(dataService.joinSession(input))
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.GiveAnswer.ENDPOINT)
    fun giveAnswer(request: HttpServletRequest, @RequestBody input: GiveAnswerInputModel): ResponseEntity<Any> {
        return ResponseEntity.ok().body(dataService.giveAnswer(input))
    }
}

