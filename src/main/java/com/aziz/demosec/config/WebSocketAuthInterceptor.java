package com.aziz.demosec.config;

import com.aziz.demosec.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Intercepteur WebSocket pour extraire et valider le JWT token.
 * 
 * Cela permet à Spring Security de savoir qui est connecté via WebSocket.
 */
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Traiter uniquement les CONNECT messages
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extraire le token JWT du header
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                try {
                    // Extraire l'email et le userId du token
                    String username = jwtService.extractEmail(token);
                    Long userId = jwtService.extractUserId(token);
                    
                    // Utiliser le userId.toString() comme principal pour convertAndSendToUser
                    String principalName = (userId != null) ? userId.toString() : username;
                    
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            principalName,
                            null,
                            new ArrayList<>()
                    );
                    
                    // Définir l'authentification dans le contexte
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    accessor.setUser(auth);
                    
                    System.out.println("✅ WebSocket authenticated for user: " + principalName + " (email: " + username + ")");
                } catch (Exception e) {
                    System.err.println("❌ Error validating JWT token: " + e.getMessage());
                }
            } else {
                System.err.println("⚠️  No JWT token provided for WebSocket connection");
            }
        }

        return message;
    }
}

