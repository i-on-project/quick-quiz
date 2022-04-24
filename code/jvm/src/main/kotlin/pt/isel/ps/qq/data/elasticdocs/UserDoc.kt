package pt.isel.ps.qq.data.elasticdocs

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "users")
data class UserDoc(
    @Id
    val userName: String,
    val displayName: String? = null,
    val loginToken: String,
    val tokenExpireDate: Long,
    val status: String? = null,
    val templates: List<String> = emptyList(),
    val sessionHistory: List<String> = emptyList()
) {
    //constructor(user: UserDoc): this(userName)
}

