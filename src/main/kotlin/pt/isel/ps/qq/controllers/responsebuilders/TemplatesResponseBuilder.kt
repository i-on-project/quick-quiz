package pt.isel.ps.qq.controllers.responsebuilders

import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.repositories.docs.TemplateDoc
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.utils.getBaseUrlHostFromRequest

@Component
class TemplatesResponseBuilder {

    fun getAllTemplatesResponse(page: Int, host: String, total: Long, lastPage: Int, templates: List<TemplateDoc>): SirenModel {

        val links = mutableListOf<SirenLink>()
        links.add(SirenLink(rel = listOf("first"), href = Uris.API.Web.V1_0.Auth.Template.url(host, 0)))
        links.add(SirenLink(rel = listOf("last"), href = Uris.API.Web.V1_0.Auth.Template.url(host, lastPage)))
        if (page < lastPage) {
            links.add(SirenLink(rel = listOf("next"), href = Uris.API.Web.V1_0.Auth.Template.url(host, page + 1)))
        }
        if (page > 0) {
            links.add(SirenLink(rel = listOf("prev"), href = Uris.API.Web.V1_0.Auth.Template.url(host, page - 1)))
        }
        return  SirenModel(
            clazz = listOf("ListTemplate"),
            properties = ListInfo(size = templates.size, total = total.toInt()),
            entities = templates.map {
                SirenEntity(
                    clazz = listOf("Template"),
                    rel = listOf("self"),
                    properties = TemplateOutputModel(it)
                )
            },
            links = links
        )
    }
    fun createTemplateResponse(templateId: String, baseUrl: String): SirenModel {
        return SirenModel(
            clazz = listOf("CreateTemplate"),
            properties = Acknowledge.TRUE,
            title = "Template successfully created.",
            links = listOf(
                SirenLink(
                    rel = listOf("self"),
                    href = Uris.API.Web.V1_0.Auth.Template.Id.url(baseUrl, templateId)
                )
            )
        )
    }
    fun getTemplateResponse(template: TemplateDoc, baseUrl: String): SirenModel {
        return SirenModel(
            clazz = listOf("Template"),
            properties = template,
            actions = listOf(
                SirenAction(
                    name = "Delete-Template",
                    title = "Delete",
                    method = SirenSupportedMethods.DELETE,
                    href = Uris.API.Web.V1_0.Auth.Template.Id.url(baseUrl,template.id)
                )//todo: missing update
            )
        )
    }
    fun deleteTemplateResponse(): SirenModel {
        return SirenModel(
            clazz = listOf("DeleteTemplate"),
            properties = Acknowledge.TRUE,
            title = "Template was deleted successfully"
        )
    }

}