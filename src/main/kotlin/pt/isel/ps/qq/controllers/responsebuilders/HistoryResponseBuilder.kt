package pt.isel.ps.qq.controllers.responsebuilders

import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.repositories.docs.HistoryDoc
import pt.isel.ps.qq.utils.Uris

@Component
class HistoryResponseBuilder {
    fun getAllHistoryResponse(
        idx: Int,
        host: String,
        total: Long,
        lastPage: Int,
        history: List<HistoryDoc>
    ): SirenModel{
        val links = mutableListOf<SirenLink>()
        links.add(SirenLink(rel = listOf("first"), href = Uris.API.Web.V1_0.Auth.History.url(host, 0)))
        links.add(SirenLink(rel = listOf("last"), href = Uris.API.Web.V1_0.Auth.History.url(host, lastPage)))
        if (idx < lastPage) {
            links.add(SirenLink(rel = listOf("next"), href = Uris.API.Web.V1_0.Auth.History.url(host, idx + 1)))
        }
        if (idx > 0) {
            links.add(SirenLink(rel = listOf("prev"), href = Uris.API.Web.V1_0.Auth.History.url(host, idx - 1)))
        }
        return SirenModel(
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
    }
}