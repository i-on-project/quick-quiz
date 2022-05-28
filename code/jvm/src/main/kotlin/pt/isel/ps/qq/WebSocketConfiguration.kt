package pt.isel.ps.qq

import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.stereotype.Component
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer


@Component
@EnableWebSocketMessageBroker
class WebSocketConfiguration : WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(stompEndpointRegistry: StompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/insession")
            .setAllowedOrigins("http://localhost:3000")
            .withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic/")
        registry.setApplicationDestinationPrefixes("/app")
    }

/*    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/session_quiz").withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker(MESSAGE_PREFIX)
        registry.setApplicationDestinationPrefixes("/app")
    }

    companion object {
        const val MESSAGE_PREFIX = "/topic/updatedQuiz"
    }*/
}