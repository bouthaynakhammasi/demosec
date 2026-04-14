package com.aziz.demosec.controller;

import com.aziz.demosec.service.WebSocketNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketControllerTest {

    @Mock
    private WebSocketNotificationService webSocketNotificationService;

    @InjectMocks
    private WebSocketController webSocketController;

    @Test
    void health_ShouldReturnStatusOk() {
        // Arrange
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testuser");

        // Act
        Map<String, String> result = webSocketController.health(principal);

        // Assert
        assertEquals("ok", result.get("status"));
        assertEquals("testuser", result.get("user"));
        assertNotNull(result.get("timestamp"));
    }

    @Test
    void health_ShouldReturnAnonymous_WhenPrincipalIsNull() {
        // Act
        Map<String, String> result = webSocketController.health(null);

        // Assert
        assertEquals("ok", result.get("status"));
        assertEquals("anonymous", result.get("user"));
    }
}
