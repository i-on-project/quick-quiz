package pt.isel.ps.qq.controllers

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.data.GiveAnswer
import pt.isel.ps.qq.data.JoinSession
import pt.isel.ps.qq.data.User


@RestController
@RequestMapping("/api/web/v1.0/guest")
class GuestController {

    @PostMapping("/join_session")
    fun guestJoinSession(inputModel: JoinSession): JoinSession {
        return inputModel
    }

    @PostMapping("/answer")
    fun guestGiveAnswer(inputModel: GiveAnswer): GiveAnswer {
        return inputModel
    }


    @PostMapping("/register")
    fun guestRegister(inputModel: User): User {
        return inputModel
    }
}

