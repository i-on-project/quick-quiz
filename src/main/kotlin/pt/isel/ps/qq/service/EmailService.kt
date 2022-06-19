package pt.isel.ps.qq.service

import com.sendgrid.*;
import java.io.IOException;

class EmailService {

    fun sendEmail(linkForLogin: String, toEmail: String): Response {
        val from = Email("ionquickquiz@gmail.com");
        val subject = "Log In to QuickQuiz";
        val to = Email(toEmail);
        val content = Content("text/plain", "Please click here to login to QuickQuiz: ${linkForLogin}");
        val mail = Mail(from, subject, to, content);

        val sg = SendGrid(System.getenv("SENDGRID_API_KEY")) //TODO: create environment variable
        val request = Request();
        try {
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            var response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
            return response
        } catch (ex: IOException) {
            throw ex;
        }
    }
}