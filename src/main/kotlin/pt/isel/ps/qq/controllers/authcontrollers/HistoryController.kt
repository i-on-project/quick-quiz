package pt.isel.ps.qq.controllers.authcontrollers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.qq.UserInfoScope
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
class HistoryController(
    private val service: HistoryService,
    private val scope: UserInfoScope
) : AuthMainController() {
    
    @GetMapping(Uris.API.Web.V1_0.Auth.History.ENDPOINT)
    fun getHistory(request: HttpServletRequest, @RequestParam page: Int?): ResponseEntity<Any> {
        val idx = page ?: 0
        val history = service.getHistory(scope.getUser().userName, idx)
        val host = getBaseUrlHostFromRequest(request)
        val total = service.historyDocumentCount()
        val lastPage = calculateLastPage(total)
        val links = mutableListOf<SirenLink>()
        links.add(SirenLink(rel = listOf("first"), href = Uris.API.Web.V1_0.Auth.History.url(host, 0)))
        links.add(SirenLink(rel = listOf("last"), href = Uris.API.Web.V1_0.Auth.History.url(host, lastPage)))
        if (idx < lastPage) {
            links.add(SirenLink(rel = listOf("next"), href = Uris.API.Web.V1_0.Auth.History.url(host, idx + 1)))
        }
        if (idx > 0) {
            links.add(SirenLink(rel = listOf("prev"), href = Uris.API.Web.V1_0.Auth.History.url(host, idx - 1)))
        }
        val body = SirenModel(
            clazz = listOf("List", "History"),
            properties = ListInfo(size = history.size, total = total.toInt()),
            entities = history.map {
                SirenEntity(
                    clazz = listOf("History"),
                    rel = listOf("item"),
                    properties = HistoryOutputModel(it),
                    fields = listOf(SirenField(name = "id", value = it.id))
                )
            },
            links = links
        )
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }


}