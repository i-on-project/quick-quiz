package pt.isel.ps.qq.controllers.authcontrollers

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import pt.isel.ps.qq.UserInfoScope
import pt.isel.ps.qq.controllers.ExceptionsResponseHandler
import pt.isel.ps.qq.controllers.responsebuilders.TemplatesResponseBuilder
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.service.TemplateService
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.utils.calculateLastPage
import pt.isel.ps.qq.utils.getBaseUrlHostFromRequest
import javax.servlet.http.HttpServletRequest

@Controller("TemplatesController")
@RequestMapping(Uris.API.Web.V1_0.Auth.PATH)
class TemplatesController (private val service: TemplateService,
                           private val scope: UserInfoScope,
                           private val exHandler: ExceptionsResponseHandler,
                           private val templatesResponseBuilder: TemplatesResponseBuilder
) {


    @GetMapping(Uris.API.Web.V1_0.Auth.Template.ENDPOINT)
    fun getAllTemplatesFromUser(request: HttpServletRequest, @RequestParam pageNumber: Int?): ResponseEntity<Any> {
        val page = pageNumber ?: 0
        val templates = service.getAllTemplates(scope.getUser().userName, page)
        val total = service.templatesDocumentsCount(scope.getUser().userName)
        val body = templatesResponseBuilder.getAllTemplatesResponse(page, getBaseUrlHostFromRequest(request), total, calculateLastPage(total), templates)
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @PostMapping(Uris.API.Web.V1_0.Auth.Template.ENDPOINT)
    fun createTemplate(request: HttpServletRequest, @RequestBody input: CreateTemplateInputModel): ResponseEntity<Any> {
        val template = service.createTemplate(scope.getUser().userName, input)
        val body = templatesResponseBuilder.createTemplateResponse(template.id, getBaseUrlHostFromRequest(request))
        return ResponseEntity.created(Uris.API.Web.V1_0.Auth.Template.Id.make(template.id))
            .contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @GetMapping(Uris.API.Web.V1_0.Auth.Template.Id.CONTROLLER_ENDPOINT)
    fun getTemplate(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val template = service.getTemplateValidatingOwner(scope.getUser().userName, id)
        val body = templatesResponseBuilder.getTemplateResponse(template, getBaseUrlHostFromRequest(request))
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }

    @DeleteMapping(Uris.API.Web.V1_0.Auth.Template.Id.CONTROLLER_ENDPOINT)
    fun deleteTemplate(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        service.deleteTemplate(scope.getUser().userName, id)
        val body = templatesResponseBuilder.deleteTemplateResponse()
        return ResponseEntity.ok().contentType(SirenModel.MEDIA_TYPE).body(body)
    }
}