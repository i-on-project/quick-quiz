package pt.isel.ps.qq.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.data.ErrorDto
import pt.isel.ps.qq.data.UserDto
import pt.isel.ps.qq.exceptions.AlreadyExistsException
import pt.isel.ps.qq.service.GuestService

@RestController
@RequestMapping("/api/web/v1.0/guest")
class GuestController(
    private val service: GuestService
) {

    companion object {
        const val USER_DISPLAY_NAME_NOT_NULL = "displayName required"
        const val USER_USERNAME_ALREADY_EXISTS = "username already exists"
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody user: UserDto): ResponseEntity<Any> {
        if(user.displayName == null) {
            return ResponseEntity
                .badRequest()
                .body(ErrorDto(code = HttpStatus.BAD_REQUEST.value(), reason = USER_DISPLAY_NAME_NOT_NULL))
        }
        return try {
            ResponseEntity.ok().body(service.registerUser(user))
        } catch(e: AlreadyExistsException) {
            ResponseEntity.badRequest().body(
                ErrorDto(code = HttpStatus.BAD_REQUEST.value(), reason = USER_USERNAME_ALREADY_EXISTS)
            )
        }
    }
}

