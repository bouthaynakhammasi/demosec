package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Notification;
import com.aziz.demosec.Entities.NotificationType;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.pharmacy.NotificationResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebSocketNotificationServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private INotificationService notificationService;

    @InjectMocks
    private WebSocketNotificationService webSocketNotificationService;

    private Notification notification;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(100L);
        user.setEmail("user@test.com");

        notification = Notification.builder()
                .id(10L)
                .recipient(user)
                .title("Alert")
                .message("Message")
                .type(NotificationType.ORDER_CREATED)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void notifyUser_ShouldSendToUserQueue() {
        // Act
        webSocketNotificationService.notifyUser(100L, notification);

        // Assert
        verify(messagingTemplate).convertAndSendToUser(
                eq("100"),
                eq("/queue/notifications"),
                any(NotificationResponseDTO.class)
        );
    }

    @Test
    void broadcastNotification_ShouldSendToTopic() {
        // Act
        webSocketNotificationService.broadcastNotification(notification);

        // Assert
        verify(messagingTemplate).convertAndSend(
                eq("/topic/notifications"),
                any(NotificationResponseDTO.class)
        );
    }

    @Test
    void notifyOrderStatusUpdate_ShouldSendToOrderUpdatesQueue() {
        // Act
        webSocketNotificationService.notifyOrderStatusUpdate(100L, 5L, "SHIPPED", "Order SHIPPED");

        // Assert
        verify(messagingTemplate).convertAndSendToUser(
                eq("100"),
                eq("/queue/order-updates"),
                anyMap()
        );
    }
}
