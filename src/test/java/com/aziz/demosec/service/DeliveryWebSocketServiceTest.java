package com.aziz.demosec.service;

import com.aziz.demosec.dto.pharmacy.DeliveryResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryWebSocketServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private DeliveryWebSocketService webSocketService;

    @Test
    void broadcastDeliveryUpdate_ShouldSendMessageToCorrectTopic() {
        // Arrange
        Long orderId = 123L;
        DeliveryResponseDTO update = DeliveryResponseDTO.builder()
                .status(com.aziz.demosec.Entities.DeliveryStatus.IN_TRANSIT)
                .currentLat(36.8)
                .currentLng(10.2)
                .build();

        // Act
        webSocketService.broadcastDeliveryUpdate(orderId, update);

        // Assert
        verify(messagingTemplate).convertAndSend(eq("/topic/delivery/123"), eq(update));
    }
}
