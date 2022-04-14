package pt.isel.ps.qq.service.controllers

import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

class User
@Component
class Service {
    fun createSession(user: User) {}
}

@RestController
@RequestMapping("/api/web/v1.0/user/{user}")
class RegisteredController(
    private val service: Service
) {

    // Has to be authenticated
    @PostMapping("/create_session")
    fun createSessionHandler(@PathVariable user: User) {
        service.createSession(user)
    }
}