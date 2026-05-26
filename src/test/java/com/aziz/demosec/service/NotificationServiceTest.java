package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Notification;
import com.aziz.demosec.Entities.NotificationType;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.pharmacy.NotificationResponseDTO;
import com.aziz.demosec.repository.NotificationRepository;
import com.aziz.demosec.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        notification = Notification.builder()
                .id(10L)
                .recipient(user)
                .title("Test Title")
                .message("Test Message")
                .isRead(false)
                .type(NotificationType.ORDER_CREATED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getForUser_ShouldReturnList() {
        // Arrange
        when(notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(notification));

        // Act
        List<NotificationResponseDTO> results = notificationService.getForUser(1L);

        // Assert
        assertEquals(1, results.size());
        assertEquals("Test Title", results.get(0).getTitle());
    }

    @Test
    void markAsRead_ShouldUpdateFlag() {
        // Arrange
        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        NotificationResponseDTO result = notificationService.markAsRead(10L);

        // Assert
        assertTrue(result.isRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void notifyAccountActivated_ShouldSaveAndSendWS() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> {
            Notification n = i.getArgument(0);
            n.setId(11L);
            return n;
        });

        // Act
        notificationService.notifyAccountActivated(user);

        // Assert
        verify(notificationRepository).save(any(Notification.class));
        verify(messagingTemplate).convertAndSendToUser(
                eq("test@example.com"),
                eq("/queue/notifications"),
                any(NotificationResponseDTO.class)
        );
    }
}
