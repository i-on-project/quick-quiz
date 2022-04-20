package pt.isel.ps.qq.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pt.isel.ps.qq.data.dto.UserDto
import pt.isel.ps.qq.data.dto.UserTokenDto
import pt.isel.ps.qq.database.UserElasticRepository
import pt.isel.ps.qq.database.elasticdocs.UserDoc
import pt.isel.ps.qq.exceptions.*
import java.util.*

//TODO: elastic time triggers ?! -> status management

@Component
class GuestService() {

    @Autowired
    private val userRepo: UserElasticRepository? = null

    companion object {
        const val TOKEN_TIMEOUT: Long = 604800
        const val REGISTRATION_TOKEN_TIMEOUT: Long = 3600
    }

    fun register(dto: UserDto): UserTokenDto {
        try {
            if (userRepo?.findById(dto.userName) != null) throw AlreadyExistsException()
        } catch (e: Exception) {
            println(e)
        }
        val uid = UUID.randomUUID()
        if (dto.displayName == null) throw java.lang.IllegalStateException("This should NEVER happen") //done on the registerInputModel
        val user = UserDoc(userName = dto.userName, displayName = dto.displayName, loginToken = uid.toString(), tokenExpireDate = getRegistrationTimeout(), status = "pending validation")
        userRepo?.save(user)
        return UserTokenDto(token = uid.toString(), dto)
    }

    fun logmein(dto: UserTokenDto): UserTokenDto {
        val user = userRepo?.findById(dto.user.userName)!!.get()

        validateUserRegistrationInfo(user)
        validateUserStatus(user)
        validateLoginToken(user, dto)

        val otherUid = UUID.randomUUID().toString()
        val timeout = getTimeout()
        val token = UserTokenDto(otherUid, user = UserDto(user.userName, user.displayName), timeout)
        userRepo.save(UserDoc(dto.user.userName, user.displayName, otherUid, timeout,"enabled"))
        return token
    }

    private fun validateLoginToken(user: UserDoc, dto: UserTokenDto) {
        if(user.loginToken != dto.token) throw InvalidTokenException()
    }

    private fun validateUserRegistrationInfo(user: UserDoc) {
        if(!validTokenTimeOut(user.tokenExpireDate) && user.status == "pending validation"){
            userRepo?.delete(user)
            throw RegistrationTimedOutException()
        }
    }

    private fun validTokenTimeOut(tokenExpireDate: Long?): Boolean =
        !(tokenExpireDate == null || getCurrentTimeInSeconds() > tokenExpireDate);

    fun requestLogin(dto: UserDto): UserTokenDto {
        val user = userRepo?.findById(dto.userName)!!.get()
        validateUserStatus(user)
        val uid = UUID.randomUUID()
        val timeout = getTimeout()
        val updatedUser = UserDoc(
            userName = user.userName,
            displayName = user.displayName,
            loginToken = uid.toString(),
            tokenExpireDate = timeout
        )
        val t = userRepo?.save(updatedUser)
        return UserTokenDto(
            token = uid.toString(),
            user = UserDto(userName = user.userName, displayName = user.displayName),
            expireDate = timeout
        )
    }

    private fun validateUserStatus(user: UserDoc) {
        when(user.status){
            "pending validation"-> throw PendingValidationException()
            "disabled"-> throw UserDisabledException()
        }
    }

    private fun getTimeout(): Long = (System.currentTimeMillis() / 1000) + TOKEN_TIMEOUT
    private fun getRegistrationTimeout(): Long = (System.currentTimeMillis() / 1000) + REGISTRATION_TOKEN_TIMEOUT
    private fun getCurrentTimeInSeconds(): Long = (System.currentTimeMillis() / 1000)
}