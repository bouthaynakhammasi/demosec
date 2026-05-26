package com.aziz.demosec.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple broker for both /topic (broadcast) and /queue (private messages)
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Prefix for messages sent FROM clients TO the server
        registry.setApplicationDestinationPrefixes("/app");
        
        // Prefix for user-specific destinations (convertAndSendToUser)
        // This will translate /user/username/queue/notifications to /queue/notifications for that user
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint with SockJS fallback
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Ajouter l'intercepteur pour valider le JWT token
        registration.interceptors(webSocketAuthInterceptor);
    }
}
