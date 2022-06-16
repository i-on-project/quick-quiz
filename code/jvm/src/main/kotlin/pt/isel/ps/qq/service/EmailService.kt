package pt.isel.ps.qq.service

import com.sendgrid.*;
import java.io.IOException;

class EmailService {

    fun sendEmail(linkForLogin: String): Response {
        val from = Email("ionquickquiz@gmail.com");
        val subject = "Sending with SendGrid is Fun";
        val to = Email("mexia.vitor@gmail.com");
        val content = Content("text/plain", "Please click here to login to QuickQuiz: ${linkForLogin}");
        val mail = Mail(from, subject, to, content);

        val sg = SendGrid("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") //TODO: create environment variable
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