package pt.isel.ps.qq

import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope
import pt.isel.ps.qq.repositories.docs.UserDoc

@Component
@RequestScope
class UserInfoScope {

    private var user: UserDoc? = null

    fun getUser(): UserDoc = user!!
    fun setUser(user: UserDoc) { this.user = user }
    fun getUserOrNull(): UserDoc? = user
}