package pt.isel.ps.qq.service

import org.springframework.stereotype.Service
import pt.isel.ps.qq.data.LoginInputModel
import pt.isel.ps.qq.data.LoginMeInputModel
import pt.isel.ps.qq.data.RegisterInputModel
import pt.isel.ps.qq.data.elasticdocs.UserDoc
import pt.isel.ps.qq.data.elasticdocs.UserStatus
import pt.isel.ps.qq.exceptions.*
import pt.isel.ps.qq.repositories.UserElasticRepository
import pt.isel.ps.qq.utils.Uris
import pt.isel.ps.qq.utils.getCurrentTimeSeconds
import java.net.URI
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

//TODO: elastic time triggers ?! -> status management

@Service
class AuthenticationService(
    private val userRepo: UserElasticRepository, private val mailSession: Session
) {

    companion object {
        // Login token default timeout time in seconds (1 week)
        const val TOKEN_TIMEOUT: Long = 604800

        // Register token default timeout time in seconds (1 hour)
        const val REGISTRATION_TOKEN_TIMEOUT: Long = 3600
    }

    fun register(input: RegisterInputModel): UserDoc {
        val registeredUser = userRepo.findById(input.userName)

        if(!registeredUser.isEmpty) {

            /*
            If user exists but the login token has already expired without a single login,
            it is permitted to register again
             */

            val user = registeredUser.get()
            if(user.status!! == UserStatus.PENDING_REGISTRATION && getCurrentTimeSeconds() > user.tokenExpireDate) {
                val newUser = UserDoc(user, UUID.randomUUID().toString(), getRegistrationTimeout())
                userRepo.save(newUser)
            } else {
                throw UserAlreadyExistsException("This email is already registered")
            }
        }
        val uid = UUID.randomUUID()

        val user = UserDoc(
            userName = input.userName,
            displayName = input.displayName,
            loginToken = uid.toString(),
            tokenExpireDate = getRegistrationTimeout(),
            status = UserStatus.PENDING_REGISTRATION
        )
        return userRepo.save(user)
    }

    private fun sendEmail(to: String) {
        val inetAdr = InternetAddress(to)
        try {
            val msg = MimeMessage(mailSession)
            msg.setFrom(InternetAddress("quick.quiz@localhost.com"))
            msg.addRecipient(Message.RecipientType.TO, inetAdr)
            msg.subject = "Quick Quiz App verify registration"
            msg.setText("Experimental")
            Transport.send(msg)
        } catch(ex: SendFailedException) {
            val instance = ErrorInstance(Uris.API.Web.V1_0.NonAuth.Register.make(), to)
            val email = ex.invalidAddresses.find { it == inetAdr }
            if(email != null) throw InvalidMailException(to, instance)
            else throw ServerMailException(to, instance)
        } catch(ex: MessagingException) {
            throw ServerMailException(to, ErrorInstance(Uris.API.Web.V1_0.NonAuth.Register.make(), to))
        }
    }

    fun requestLogin(userName: LoginInputModel): UserDoc {
        val opt = userRepo.findById(userName.userName)
        if(opt.isEmpty) throw UserNotFoundException()
        val user = opt.get()

        validateUserStatusIsNotPending(user, Uris.API.Web.V1_0.NonAuth.Login.make())
        validateUserStatusIsNotDisabled(user, Uris.API.Web.V1_0.NonAuth.Login.make())

        val uid = UUID.randomUUID()
        val updatedUser = UserDoc(user, uid.toString(), getTokenTimeout())
        return userRepo.save(updatedUser)
    }

    fun logmein(input: LoginMeInputModel): UserDoc {
        val opt = userRepo.findById(input.userName)
        if(opt.isEmpty) throw UserNotFoundException()
        val user = opt.get()

        validateUserRegistrationInfo(user)
        validateUserStatusIsNotDisabled(user, Uris.API.Web.V1_0.NonAuth.Logmein.make())
        validateLoginToken(user, input.loginToken)

        val otherUid = UUID.randomUUID().toString()
        val timeout = getTokenTimeout()
        val updatedUser = UserDoc(input.userName, user.displayName, otherUid, timeout, UserStatus.ENABLED)
        return userRepo.save(updatedUser)
    }

    private fun validateLoginToken(user: UserDoc, inputToken: String) {
        if(user.loginToken != inputToken) throw InvalidTokenException()
        if(getCurrentTimeSeconds() > user.tokenExpireDate) throw TokenExpiredException()
    }

    private fun validateUserRegistrationInfo(user: UserDoc) {
        if(user.status == UserStatus.PENDING_REGISTRATION && !validTokenTimeOut(user.tokenExpireDate)) {
            userRepo.delete(user)
            throw TokenExpiredException()
        }
    }

    private fun validTokenTimeOut(tokenExpireDate: Long?): Boolean {
        return !(tokenExpireDate == null || getCurrentTimeSeconds() > tokenExpireDate)
    }

    private fun validateUserStatusIsNotDisabled(user: UserDoc, method: URI) {
        if(user.status == UserStatus.DISABLED) throw UserDisabledException("Your email was disabled")
    }

    private fun validateUserStatusIsNotPending(user: UserDoc, method: URI) {
        if(user.status == UserStatus.PENDING_REGISTRATION) throw PendingValidationException("Your email is pending")
    }

    private fun getTokenTimeout(): Long = getCurrentTimeSeconds() + TOKEN_TIMEOUT
    private fun getRegistrationTimeout(): Long = getCurrentTimeSeconds() + REGISTRATION_TOKEN_TIMEOUT

}