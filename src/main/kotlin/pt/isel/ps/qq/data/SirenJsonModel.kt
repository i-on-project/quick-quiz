package pt.isel.ps.qq.data

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.MediaType
import javax.validation.constraints.NotEmpty

enum class SirenSupportedTypes(val type: String) {
    HIDDEN("hidden"),
    TEXT("text"),
    SEARCH("search"),
    TEL("tel"),
    URL("url"),
    EMAIL("email"),
    PASSWORD("password"),
    DATETIME("datetime"),
    DATE("date"),
    MONTH("month"),
    WEEK("week"),
    TIME("time"),
    DATETIME_LOCAL("datetime-local"),
    NUMBER("number"),
    RANGE("range"),
    COLOR("color"),
    CHECKBOX("checkbox"),
    RADIO("radio"),
    FILE("file")
}

enum class SirenSupportedMethods {
    GET, PUT, POST, DELETE, PATCH
}

data class SirenLink(
    @JsonProperty("class") @JsonAlias("class") val clazz: List<String> = emptyList(),
    val title: String? = null,
    @NotEmpty val rel: List<String>, //required
    val href: String,      //required
    val type: String? = null
)

data class SirenField(
    val name: String, // required
    val type: String = SirenSupportedTypes.TEXT.type,
    val title: String? = null,
    val value: Any?
)

data class SirenAction(
    @JsonProperty("class") @JsonAlias("class") val clazz: List<String> = emptyList(),
    val name: String, //required
    val method: SirenSupportedMethods = SirenSupportedMethods.POST,
    val href: String, //required
    val title: String? = null,
    val type: String = "application/x-www-form-urlencoded",
    val fields: List<SirenField> = emptyList()
)

data class SirenEntity(
    @JsonProperty("class") @JsonAlias("class") val clazz: List<String> = emptyList(),
    @NotEmpty val rel: List<String>, //required
    val href: String? = null,
    val properties: Any? = null,
    val links: List<SirenLink> = emptyList(),
    val title: String? = null,
    val fields: List<SirenField> = emptyList()
)

data class SirenModel(
    @JsonProperty("class") @JsonAlias("class") @NotEmpty val clazz: List<String>,
    val title: String? = null,
    val properties: Any? = null,
    val entities: List<SirenEntity> = emptyList(),
    val actions: List<SirenAction> = emptyList(),
    val links: List<SirenLink> = emptyList()
) {
    companion object {
        val MEDIA_TYPE = MediaType.parseMediaType("application/vnd.siren+json")
    }
}