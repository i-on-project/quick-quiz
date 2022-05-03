package pt.isel.ps.qq.data

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import pt.isel.ps.qq.utils.Uris

data class SirenLink(
    val rel: List<String>,
    val href: String
) {
    companion object {
        fun self(href: String) = SirenLink(
            rel = listOf("self"),
            href = href
        )
    }
}

data class SirenFields(
    val name: String,
    val type: String,
    val value: String? = null
)

data class SirenAction(
    val name: String,
    val title: String,
    val method: String,
    val href: String,
    val type: String = "application/x-www-form-urlencoded",
    val fields: List<SirenFields> = emptyList()
) {
    companion object {
        fun delete(toDelete: String, href: String) = SirenAction(
            name = "Delete-$toDelete",
            title = "Delete",
            method = "DELETE",
            href = href
        )
        fun update(toUpdate: String, href: String) = SirenAction(
            name = "Update-$toUpdate",
            title = "Edit",
            method = "PUT",
            href = href
        )
    }
}

data class SirenEntity(
    @JsonProperty("class")
    @JsonAlias("class")
    val clazz: List<String>,
    val rel: List<String>,
    val href: String? = null,
    val properties: Any? = null,
    val links: List<SirenLink> = emptyList()
) {
    companion object {
        fun userSirenEntity(id: String) = SirenEntity(
            clazz = listOf("User"),
            rel = listOf("self"),
            href = Uris.API.Web.V1_0.Auth.User.Id.make(id).toString()
        )
        fun quizSirenEntity(id: String) = SirenEntity(
            clazz = listOf("Quiz"),
            rel = listOf("self"),
            href = Uris.API.Web.V1_0.Auth.Quiz.Id.make(id).toString()
        )
    }
}

data class SirenJson(
    @JsonProperty("class")
    @JsonAlias("class")
    val clazz: List<String>,
    val properties: Any? = null,
    val entities: List<SirenEntity> = emptyList(),
    val actions: List<SirenAction> = emptyList(),
    val links: List<SirenLink> = emptyList(),
    val title: String? = null
)

class SirenJsonBuilder