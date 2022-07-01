package pt.isel.ps.qq.controllers.authcontrollers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.controllers.responsebuilders.HistoryResponseBuilder
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.service.HistoryService
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.utils.getBaseUrlHostFromRequest
import javax.servlet.http.HttpServletRequest

/**
 * This controller is responsible to handle the requests that require authentication.
 * In this class there is a value scope that retains the information about the user. This value is injected by the
 * UserFilter that validates the user.
 */

@RestController("HistoryController")
@RequestMapping(Uris.API.Web.V1_0.Auth.PATH)
class HistoryController(
    private val service: HistoryService,
    private val scope: UserInfoScope,
    private val historyResponseBuilder: HistoryResponseBuilder
) : AuthMainController() {
    
    @GetMapping(Uris.API.Web.V1_0.Auth.History.ENDPOINT)
    fun getHistory(request: HttpServletRequest, @RequestParam page: Int?): ResponseEntity<Any> {
        val idx = page ?: 0
        val history = service.getHistory(scope.getUser().userName, idx)
        val total = service.historyDocumentCount()
        val body = historyResponseBuilder.getAllHistoryResponse(idx, getBaseUrlHostFromRequest(request), total, calculateLastPage(total), history)
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }


}