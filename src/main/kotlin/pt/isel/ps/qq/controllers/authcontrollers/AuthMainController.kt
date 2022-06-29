package pt.isel.ps.qq.controllers.authcontrollers

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.qq.service.MainDataService
import pt.isel.ps.qq.utils.Uris
import javax.servlet.http.Cookie

@RestController("AuthController")
@RequestMapping(Uris.API.Web.V1_0.Auth.PATH)
class AuthMainController ( ) {
    fun calculateLastPage(total: Long): Int {
        return ((total.toDouble() / MainDataService.PAGE_SIZE) + 0.5).toInt()
    }

}