package pt.isel.ps.qq.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.data.dto.ErrorDto
import pt.isel.ps.qq.data.dto.input.JoinSessionInputModel
import pt.isel.ps.qq.exceptions.InvalidTokenException
import pt.isel.ps.qq.exceptions.UserNotFoundException
import pt.isel.ps.qq.service.GuestService
import pt.isel.ps.qq.service.SessionService

@RestController
@RequestMapping("/api/web/v1.0/session")
class SessionController (
    private val service: SessionService
) {

    @PostMapping("/joinsession")
    fun joinSession(@RequestBody input: JoinSessionInputModel): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(service.joinSession(input))
        } catch(e: InvalidTokenException) {
            ResponseEntity.badRequest().body(ErrorDto(code = HttpStatus.BAD_REQUEST.value(), GuestController.USER_INVALID_TOKEN))
        } catch(e: UserNotFoundException) {
            ResponseEntity.badRequest().body(ErrorDto(code = HttpStatus.BAD_REQUEST.value(), GuestController.USER_NOT_REGISTERED))
        }
    }
}