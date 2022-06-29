package pt.isel.ps.qq.controllers.unauthcontrollers

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.utils.Uris

@RestController("UnAuthController")
@RequestMapping(Uris.API.Web.V1_0.NonAuth.PATH)
class UnauthMainController
    val appHost: String = System.getenv("QQ_HOST");