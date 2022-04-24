package pt.isel.ps.qq.controllers

import org.springframework.web.bind.annotation.*
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.data.SessionInputModel
import pt.isel.ps.qq.data.elasticdocs.SessionDoc
import pt.isel.ps.qq.service.SessionService
import pt.isel.ps.qq.utils.Uris

@RestController
@RequestMapping(Uris.API.Web.V1_0.Auth.PATH)
class RegisteredController(
    private val service: SessionService,
    private val scope: UserInfoScope
) {

    @PostMapping(Uris.API.Web.V1_0.Auth.CreateSession.ENDPOINT)
    fun createSession(@RequestBody session: SessionInputModel): SessionDoc {
        return service.createSession(scope.getUser().userName, session)
    }

    @PostMapping(Uris.API.Web.V1_0.Auth.DeleteSession.ENDPOINT)
    fun deleteSession(@PathVariable id: String) {
        service.deleteSession(scope.getUser().userName, id)
    }
}