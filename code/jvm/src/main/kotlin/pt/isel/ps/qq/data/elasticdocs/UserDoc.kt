package pt.isel.ps.qq.data.elasticdocs

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

enum class UserStatus {
    PENDING_REGISTRATION, ENABLED, DISABLED;
}

@Document(indexName = "users")
data class UserDoc(
    @Id
    val userName: String,
    val displayName: String? = null,
    val loginToken: String,
    val tokenExpireDate: Long,
    val status: UserStatus? = null,
    val templates: List<String> = emptyList(),
    val sessionHistory: List<String> = emptyList()
) {
    constructor(user: UserDoc, newToken: String, newExpiredDate: Long): this(
        userName = user.userName,
        displayName = user.displayName,
        loginToken = newToken,
        tokenExpireDate = newExpiredDate,
        status = user.status,
        templates = user.templates,
        sessionHistory = user.sessionHistory
    )
}

