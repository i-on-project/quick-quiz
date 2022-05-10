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
            If user exists but the registration token has already expired without a single login,
            it is permitted to register again
             */

            val user = registeredUser.get()
            if(user.status == UserStatus.PENDING_REGISTRATION && user.registrationExpireDate != null) {
                if(getCurrentTimeSeconds() < user.registrationExpireDate){
                    // send email
                    return user
                }
            } else {
                throw UserAlreadyExistsException("This email is already registered")
            }
        }
        val uid = UUID.randomUUID()

        val user = UserDoc(
            userName = input.userName,
            displayName = input.displayName,
            status = UserStatus.PENDING_REGISTRATION,
            registrationToken = uid.toString(),
            registrationExpireDate = getRegistrationTimeout()
        )

        //send email
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
        val user = getUser(userName.userName)

        validateUserStatusIsNotPending(user)
        validateUserStatusIsNotDisabled(user)

        val uid = UUID.randomUUID()
        val updatedUser = UserDoc.userRequest(user, uid.toString(), getTokenTimeout())
        return userRepo.save(updatedUser)
    }

    fun logmein(input: LoginMeInputModel): UserDoc {
        val user = getUser(input.userName)
        validateUserStatusIsNotDisabled(user)

        if(user.status == UserStatus.PENDING_REGISTRATION) {
            // First Time login
            if(user.registrationExpireDate == null || getCurrentTimeSeconds() > user.registrationExpireDate) {
                userRepo.deleteById(user.userName)
                throw TokenExpiredException()
            }
            validExpireDate(user.registrationExpireDate)
            validateToken(user.registrationToken, input.loginToken)

            val enabledUser = UserDoc(
                userName = user.userName,
                displayName = user.displayName,
                status = UserStatus.ENABLED,
                loginToken = UUID.randomUUID().toString(),
                loginExpireDate = getTokenTimeout()
            )
            return userRepo.save(enabledUser)
        }

        validateToken(user.requestToken, input.loginToken)
        validExpireDate(user.requestExpireDate)
        val updatedUser = UserDoc.userLogin(user, UUID.randomUUID().toString(), getTokenTimeout())
        return userRepo.save(updatedUser)
    }

    fun logout(user: String) {
        val doc = getUser(user)
        validateUserStatusIsNotDisabled(doc)
        validateUserStatusIsNotPending(doc)
        userRepo.save(UserDoc(doc.userName, doc.displayName, doc.status))
    }

    private fun getUser(user: String): UserDoc {
        val opt = userRepo.findById(user)
        if(opt.isEmpty) throw UserNotFoundException()
        return opt.get()
    }

    private fun validateToken(userToken: String?, inputToken: String) {
        if(userToken == null || userToken != inputToken) throw InvalidTokenException()
    }

    private fun validExpireDate(date: Long?): Boolean {
        return !(date == null || getCurrentTimeSeconds() > date)
    }

    private fun validateUserStatusIsNotDisabled(user: UserDoc) {
        if(user.status == UserStatus.DISABLED) throw UserDisabledException("Your email was disabled")
    }

    private fun validateUserStatusIsNotPending(user: UserDoc) {
        if(user.status == UserStatus.PENDING_REGISTRATION) throw PendingValidationException("Your email is pending")
    }

    private fun getTokenTimeout(): Long = getCurrentTimeSeconds() + TOKEN_TIMEOUT
    private fun getRegistrationTimeout(): Long = getCurrentTimeSeconds() + REGISTRATION_TOKEN_TIMEOUT

//TODO: Create LOGIN Cache to requests to elastic
    fun checkUserLoginStatus(userName: String, token: String): UserDoc {
        val user = getUser(userName)
        if(user.loginToken != token) throw UserDisabledException() //TODO: create User Login Expired
        return user
    }

}