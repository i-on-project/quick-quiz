package pt.isel.ps.qq.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.data.dto.ErrorDto
import pt.isel.ps.qq.data.dto.UserDto
import pt.isel.ps.qq.data.dto.UserTokenDto
import pt.isel.ps.qq.exceptions.AlreadyExistsException
import pt.isel.ps.qq.exceptions.InvalidTokenException
import pt.isel.ps.qq.exceptions.UserNotFoundException
import pt.isel.ps.qq.service.GuestService

@RestController
@RequestMapping("/api/web/v1.0/guest")
class GuestController(
    private val service: GuestService
) {

    companion object {
        const val USER_DISPLAY_NAME_NOT_NULL = "displayName required"
        const val USER_USERNAME_ALREADY_EXISTS = "username already exists"
        const val USER_INVALID_TOKEN = "invalid token"
        const val USER_NOT_REGISTERED = "invalid user / not registered"
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody user: UserDto): ResponseEntity<Any> {
        if(user.displayName == null) {
            return ResponseEntity
                .badRequest()
                .body(ErrorDto(code = HttpStatus.BAD_REQUEST.value(), reason = USER_DISPLAY_NAME_NOT_NULL))
        }
        return try {
            ResponseEntity.ok().body(service.register(user))
        } catch(e: AlreadyExistsException) {
            ResponseEntity.badRequest().body(
                ErrorDto(code = HttpStatus.BAD_REQUEST.value(), reason = USER_USERNAME_ALREADY_EXISTS)
            )
        }
    }

    @PostMapping("/login")
    fun requestLogin(@RequestBody userName: UserDto): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(service.requestLogin(userName))
        } catch(e: InvalidTokenException) {
            ResponseEntity.badRequest().body(ErrorDto(code = HttpStatus.BAD_REQUEST.value(), USER_INVALID_TOKEN))
        } catch(e: UserNotFoundException) {
            ResponseEntity.badRequest().body(ErrorDto(code = HttpStatus.BAD_REQUEST.value(), USER_NOT_REGISTERED))
        }

    }

    @PostMapping("/logmein")
    fun loginUser(@RequestBody token: UserTokenDto): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(service.logmein(token))
        } catch(e: InvalidTokenException) {
            ResponseEntity.badRequest().body(ErrorDto(code = HttpStatus.BAD_REQUEST.value(), USER_INVALID_TOKEN))
        } catch(e: UserNotFoundException) {
            ResponseEntity.badRequest().body(ErrorDto(code = HttpStatus.BAD_REQUEST.value(), USER_NOT_REGISTERED))
        }
    }

    //TODO from this point every single registered operation needs the token an username on the header
}

