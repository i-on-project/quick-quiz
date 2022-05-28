package pt.isel.ps.qq.controllers

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import pt.isel.ps.qq.data.MessageBean


@Controller
class SocketController {

    @MessageMapping("/insession/{sessionId}")
    @SendTo("/topic/insession/{sessionId}")
    fun sendToAll(@DestinationVariable sessionId: String, @Payload message: MessageBean): MessageBean {
        println("I got called.....${sessionId}")
        return MessageBean("Question", "Something about a question happened!")
    }

    @MessageMapping("/orginsession/{sessionId}")
    @SendTo("/topic/orginsession/{sessionId}")
    fun sendToOrg(@DestinationVariable sessionId: String, @Payload message: MessageBean): MessageBean {
        println("I got called.....${sessionId}")
        return MessageBean("Answer", "Something about an answer happened!")
    }
}