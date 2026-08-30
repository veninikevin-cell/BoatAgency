package com.example.lago.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// broker STOMP/WebSocket per chat utente-fornitore e notifiche in tempo reale
// (vedi observer/WebSocketNotificationObserver per chi manda i messaggi)
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // /queue per messaggi diretti a un utente,
        // /app prefisso per i messaggi in arrivo dal client,
        // /user per instradare verso un utente specifico
        config.enableSimpleBroker("/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // SockJS come fallback per browser senza supporto WebSocket nativo
        registry.addEndpoint("/ws").withSockJS();
    }
}