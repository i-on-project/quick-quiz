package pt.isel.ps.qq.data.docs

import org.springframework.data.annotation.Id

import org.springframework.data.mongodb.core.mapping.Document

enum class UserStatus {
    PENDING_REGISTRATION, ENABLED, DISABLED;
}

@Document(collection  = "users")
data class UserDoc(
    @Id
    val userName: String,
    val displayName: String,
    val status: UserStatus,
    val registrationToken: String? = null,
    val registrationExpireDate: Long? = null,
    val requestToken: String? = null,
    val requestExpireDate: Long? = null,
    val loginToken: String? = null,
    val loginExpireDate: Long? = null,
) {
    companion object {
        fun userRegister(user: UserDoc, token: String, date: Long) = UserDoc(
            userName = user.userName,
            displayName = user.displayName,
            status = user.status,
            registrationToken = token,
            registrationExpireDate = date
        )
        fun userRequest(user: UserDoc, token: String, date: Long) = UserDoc(
            userName = user.userName,
            displayName = user.displayName,
            status = user.status,
            requestToken = token,
            requestExpireDate = date
        )
        fun userLogin(user: UserDoc, token: String, date: Long) = UserDoc(
            userName = user.userName,
            displayName = user.displayName,
            status = user.status,
            loginToken = token,
            loginExpireDate = date
        )
    }
}

