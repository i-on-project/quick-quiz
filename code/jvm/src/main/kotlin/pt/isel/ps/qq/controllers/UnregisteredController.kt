package pt.isel.ps.qq.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.service.AuthenticationService
import pt.isel.ps.qq.service.SessionService
import pt.isel.ps.qq.utils.Uris
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(Uris.API.Web.V1_0.NonAuth.PATH)
class UnregisteredController(
    private val authenticationService: AuthenticationService,
    private val sessionService: SessionService
) {

    @PostMapping(Uris.API.Web.V1_0.NonAuth.Register.ENDPOINT)
    fun registerUser(@RequestBody input: RegisterInputModel): ResponseEntity<Any> {
        return ResponseEntity.ok().body(authenticationService.register(input))
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.Login.ENDPOINT)
    fun requestLogin(@RequestBody userName: LoginInputModel): ResponseEntity<Any> {
        return ResponseEntity.ok().body(authenticationService.requestLogin(userName))
    }

    @PostMapping(Uris.API.Web.V1_0.NonAuth.Logmein.ENDPOINT)
    fun loginUser(@RequestBody input: LoginMeInputModel): ResponseEntity<Any> {
        return ResponseEntity.ok().body(authenticationService.logmein(input))
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

