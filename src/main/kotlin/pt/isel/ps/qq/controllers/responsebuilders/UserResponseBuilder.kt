package pt.isel.ps.qq.controllers.responsebuilders

import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.*
import pt.isel.ps.qq.repositories.docs.UserDoc
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.utils.getBaseUrlHostFromRequest

@Component
class UserResponseBuilder {
    fun requestLoginResponse(user: UserDoc, baseUrl: String): SirenModel {
        return SirenModel(
            clazz = listOf("RequestLogin"),
            properties = RequestLoginOutputModel(userName = user.userName, displayName = user.displayName, token = user.requestToken!!, timeout = user.requestExpireDate!!),
            actions = listOf(
                SirenAction(
                    name = "Logmein",
                    title = "Login",
                    method = SirenSupportedMethods.POST,
                    href = Uris.API.Web.V1_0.NonAuth.Logmein.url(baseUrl),
                    fields = listOf(
                        SirenField(
                            name = "userName",
                            value = user.userName
                        ), SirenField(
                            name = "loginToken",
                            value = user.loginToken
                        )
                    )
                )
            ),
            title = "Check your email"
        )
    }

    fun loginUserResponse(user: UserDoc): SirenModel {
        return SirenModel(
            clazz = listOf("Login"),
            //properties = Acknowledge.TRUE,
            properties = RequestLoginOutputModel(
                userName = user.userName,
                displayName = user.displayName,
            ),
            title = "Welcome ${user.userName}"
        )
    }

    fun registerUserResponse(user: UserDoc, baseUrl: String): SirenModel {
        return SirenModel(
            clazz = listOf("Register"),
            properties = RequestLoginOutputModel(userName = user.userName, displayName = user.displayName, token = user.registrationToken!!, timeout = user.registrationExpireDate!!),
            actions = listOf(
                SirenAction(
                    name = "Logmein",
                    title = "Login",
                    method = SirenSupportedMethods.POST,
                    href = Uris.API.Web.V1_0.NonAuth.Logmein.url(baseUrl),
                    fields = listOf( //TODO: remove
                        SirenField(
                            name = "userName",
                            value = user.userName
                        ), SirenField(
                            name = "loginToken",
                            value = user.loginToken
                        )
                    )
                )
            ),
            title = "Check your email"
        )
    }

    fun checkAuthStatus(user: UserDoc): SirenModel { //TODO: Add tags
        return SirenModel(
            clazz = listOf("Login"),
            //properties = Acknowledge.TRUE,
            properties = RequestLoginOutputModel(
                userName = user.userName,
                displayName = user.displayName,
            ),
            title = "Welcome ${user.userName}"
        )
    }
}