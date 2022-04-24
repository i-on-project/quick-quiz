package pt.isel.ps.qq.service

import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.LoginInputModel
import pt.isel.ps.qq.data.LoginMeInputModel
import pt.isel.ps.qq.data.RegisterInputModel
import pt.isel.ps.qq.repositories.SessionElasticRepository
import pt.isel.ps.qq.repositories.UserElasticRepository
import pt.isel.ps.qq.data.elasticdocs.UserDoc
import pt.isel.ps.qq.exceptions.*
import pt.isel.ps.qq.utils.getCurrentTimeSeconds
import java.util.*

//TODO: elastic time triggers ?! -> status management

@Service
class GuestService(private val userRepo: UserElasticRepository,
                   private val sessionRepo: SessionElasticRepository
) {

    companion object {
        const val TOKEN_TIMEOUT: Long = 604800
        const val REGISTRATION_TOKEN_TIMEOUT: Long = 3600
    }

    fun register(input: RegisterInputModel): UserDoc {

        try {
            val registeredUser = userRepo.findById(input.userName)
            if(!registeredUser.isEmpty) {
                val user = registeredUser.get()
                if(user.status!! == "pending validation" && getCurrentTimeSeconds() > user.tokenExpireDate) {

                } else {
                    throw AlreadyExistsException()
                }
            }
            val uid = UUID.randomUUID()

            val user = UserDoc(
                userName = input.userName,
                displayName = input.displayName,
                loginToken = uid.toString(),
                tokenExpireDate = getRegistrationTimeout(),
                status = "pending validation"
            )
            userRepo.save(user)
            return user
        } catch (e: Exception) {

            println(e) //index does not exist
            throw e;
        }
    }

    fun requestLogin(userName: LoginInputModel): UserDoc {
        val user = userRepo.findById(userName.userName)!!.get()
        validateUserStatus(user)
        val uid = UUID.randomUUID()
        val timeout = getTimeout()
        val updatedUser = UserDoc(
            userName = user.userName,
            displayName = user.displayName,
            loginToken = uid.toString(),
            tokenExpireDate = timeout
        )
        val t = userRepo.save(updatedUser)
        return updatedUser

    }

    fun logmein(input: LoginMeInputModel): UserDoc {
        val user = userRepo.findById(input.userName)!!.get()

        validateUserRegistrationInfo(user)
        validateUserStatus(user)
        validateLoginToken(user, input.loginToken)

        val otherUid = UUID.randomUUID().toString()
        val timeout = getTimeout()
        val updatedUser = UserDoc(input.userName, user.displayName, otherUid, timeout, "enabled")
        userRepo.save(updatedUser)
        return updatedUser
    }

    private fun validateLoginToken(user: UserDoc, inputToken: String) {
        if (user.loginToken != inputToken) throw InvalidTokenException()
    }

    private fun validateUserRegistrationInfo(user: UserDoc) {
        if (!validTokenTimeOut(user.tokenExpireDate) && user.status == "pending validation") {
            userRepo.delete(user)
            throw RegistrationTimedOutException()
        }
    }

    private fun validTokenTimeOut(tokenExpireDate: Long?): Boolean =
        !(tokenExpireDate == null || getCurrentTimeInSeconds() > tokenExpireDate);


    private fun validateUserStatus(user: UserDoc) {
        when (user.status) {
            //"pending validation" -> throw PendingValidationException()
            "disabled" -> throw UserDisabledException()
        }
    }



    private fun getTimeout(): Long = (System.currentTimeMillis() / 1000) + TOKEN_TIMEOUT
    private fun getRegistrationTimeout(): Long = (System.currentTimeMillis() / 1000) + REGISTRATION_TOKEN_TIMEOUT
    private fun getCurrentTimeInSeconds(): Long = (System.currentTimeMillis() / 1000)

}