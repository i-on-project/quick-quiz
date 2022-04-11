package pt.isel.ps.qq.controllers


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pt.isel.ps.qq.data.InputModel
import pt.isel.ps.qq.service.GuestService
import pt.isel.ps.qq.service.IGuestService

@RestController
@RequestMapping("/api/web/guest")
class GuestController {

    @PostMapping("/join_session")
    fun guestJoinSession(input: InputModel): InputModel {
        return input
    }


    @PostMapping("/answer")
    fun guestGiveAnswer(@RequestBody input: InputModel) {
    }

    @PostMapping("/register")
    fun guestRegister(input: InputModel) {
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException : RuntimeException() { //
}