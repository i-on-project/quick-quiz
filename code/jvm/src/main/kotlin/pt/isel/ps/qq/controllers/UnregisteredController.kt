package pt.isel.ps.qq.controllers

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.service.AuthenticationService
import pt.isel.ps.qq.service.SessionService
import pt.isel.ps.qq.utils.Uris
import java.time.Duration
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(Uris.API.Web.V1_0.NonAuth.PATH)
class UnregisteredController(
    private val authenticationService: AuthenticationService,
    private val sessionService: SessionService
) {

    @PostMapping(Uris.API.Web.V1_0.NonAuth.Register.ENDPOINT)
    fun registerUser(@RequestBody input: RegisterInputModel): ResponseEntity<Any> {
        val user = authenticationService.register(input)
        val body = SirenJson(
            clazz = listOf("Register"),
            properties = user, // TODO: null
            title = "Check your email"
        )
        return ResponseEntity.ok().body(body)
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.Login.ENDPOINT)
    fun requestLogin(@RequestBody userName: LoginInputModel): ResponseEntity<Any> {
        val user = authenticationService.requestLogin(userName)
        val body = SirenJson(
            clazz = listOf("RequestLogin"),
            properties = user, // TODO: null
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
        headers.add("Set-Cookie", "Authorization=$base64; Max-Age=${Duration.ofDays(7).toSeconds()}; Path=/; Secure; HttpOnly; SameSite=Strict")

        val body = SirenJson(
            clazz = listOf("Login"),
            properties = doc, //TODO null
            entities = listOf(SirenEntity.userSirenEntity(doc.userName)),
        )

        return ResponseEntity.ok().headers(headers).body(body)
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.JoinSession.ENDPOINT)
    fun joinSession(@RequestBody input: JoinSessionInputModel): ResponseEntity<Any> {
        return ResponseEntity.ok().body(sessionService.joinSession(input))
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.GiveAnswer.ENDPOINT)
    fun giveAnswer(request: HttpServletRequest, @RequestBody input: GiveAnswerInputModel): ResponseEntity<Any> {
        return ResponseEntity.ok().body(sessionService.giveAnswer(input))
    }
}

