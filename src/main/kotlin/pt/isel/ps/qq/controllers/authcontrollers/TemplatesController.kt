package pt.isel.ps.qq.controllers.authcontrollers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.controllers.ExceptionsResponseHandler
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.service.DataService
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.utils.getBaseUrlHostFromRequest
import javax.servlet.http.HttpServletRequest

@RestController("TemplatesController")
class TemplatesController (private val service: DataService,
                           private val scope: UserInfoScope,
                           private val exHandler: ExceptionsResponseHandler
) : AuthMainController()  {


    @GetMapping(Uris.API.Web.V1_0.Auth.Template.ENDPOINT)
    fun getAllTemplatesFromUser(request: HttpServletRequest, @RequestParam pageNumber: Int?): ResponseEntity<Any> {
        val page = pageNumber ?: 0
        val list = service.getAllTemplates(scope.getUser().userName, page)
        val host = getBaseUrlHostFromRequest(request)
        val links = mutableListOf<SirenLink>()
        links.add(SirenLink(rel = listOf("first"), href = Uris.API.Web.V1_0.Auth.Template.url(host, 0)))
        val total = service.templatesDocumentsCount()
        val lastPage = calculateLastPage(total)
        links.add(SirenLink(rel = listOf("last"), href = Uris.API.Web.V1_0.Auth.Template.url(host, lastPage)))
        if (page < lastPage) {
            links.add(SirenLink(rel = listOf("next"), href = Uris.API.Web.V1_0.Auth.Template.url(host, page + 1)))
        }
        if (page > 0) {
            links.add(SirenLink(rel = listOf("prev"), href = Uris.API.Web.V1_0.Auth.Template.url(host, page - 1)))
        }
        val body = SirenModel(
            clazz = listOf("ListTemplate"),
            properties = ListInfo(size = list.size, total = total.toInt()),
            entities = list.map {
                SirenEntity(
                    clazz = listOf("Template"),
                    rel = listOf("self"),
                    properties = TemplateOutputModel(it)
                )
            },
            links = links
        )
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @PostMapping(Uris.API.Web.V1_0.Auth.Template.ENDPOINT)
    fun createTemplate(request: HttpServletRequest, @RequestBody input: CreateTemplateInputModel): ResponseEntity<Any> {
        val doc = service.createTemplate(scope.getUser().userName, input)
        val body = SirenModel(
            clazz = listOf("CreateTemplate"),
            properties = Acknowledge.TRUE,
            title = "Template successfully created.",
            links = listOf(
                SirenLink(
                    rel = listOf("self"),
                    href = Uris.API.Web.V1_0.Auth.Template.Id.url(getBaseUrlHostFromRequest(request), doc.id)
                )
            )
        )
        return ResponseEntity.created(Uris.API.Web.V1_0.Auth.Template.Id.make(doc.id))
            .contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @GetMapping(Uris.API.Web.V1_0.Auth.Template.Id.CONTROLLER_ENDPOINT)
    fun getTemplate(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val doc = service.getTemplateValidatingOwner(scope.getUser().userName, id)
        val body = SirenModel(
            clazz = listOf("Template"),
            properties = doc,
            actions = listOf(
                SirenAction(
                    name = "Delete-Template",
                    title = "Delete",
                    method = SirenSupportedMethods.DELETE,
                    href = Uris.API.Web.V1_0.Auth.Template.Id.url(getBaseUrlHostFromRequest(request), id)
                )
            )
        )
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @DeleteMapping(Uris.API.Web.V1_0.Auth.Template.Id.CONTROLLER_ENDPOINT)
    fun deleteTemplate(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        service.deleteTemplate(scope.getUser().userName, id)
        val body = SirenModel(
            clazz = listOf("DeleteTemplate"),
            properties = Acknowledge.TRUE,
            title = "Template was deleted successfully"
        )
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }
}