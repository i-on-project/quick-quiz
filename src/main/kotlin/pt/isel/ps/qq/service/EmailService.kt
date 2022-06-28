package pt.isel.ps.qq.service

import com.sendgrid.*;
import org.springframework.stereotype.Component
import java.io.IOException;

@Component
class EmailService {
    companion object {
        val SENDGRID_API_KEY = SendGrid(System.getenv("SENDGRID_API_KEY"))
        val FROM = Email("ionquickquiz@gmail.com")
        const val SUBJECT = "Authenticate to QuickQuiz"
        const val CONTENT_TYPE = "text/plain"
        const val BODY_MESSAGE = "Please click here to login to QuickQuiz: "
        const val END_POINT = "mail/send"
    }
    fun sendEmail(linkForLogin: String, toEmail: String): Response {

        val to = Email(toEmail)
        val content = Content(CONTENT_TYPE, "${BODY_MESSAGE} ${linkForLogin}")
        val mail = Mail(FROM, SUBJECT, to, content);
        val request = Request()
        try {
            request.method = Method.POST
            request.endpoint = END_POINT
            request.body = mail.build()

            return SENDGRID_API_KEY.api(request)
        } catch (ex: IOException) {
            throw ex
        }
    }
}