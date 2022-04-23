package pt.isel.ps.qq.controllers

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.data.dto.input.SessionInputModel
import pt.isel.ps.qq.repositories.elasticdocs.SessionDoc
import pt.isel.ps.qq.service.UserService

@RestController
@RequestMapping("/api/web/v1.0/user")
class UserController(
    private val service: UserService
) {

    @PostMapping("/create_session")
    fun createSession(@RequestBody session: SessionInputModel): SessionDoc {
        return service.createSession(session)
    }
}