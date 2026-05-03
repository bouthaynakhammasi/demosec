package com.aziz.demosec.service;

import com.aziz.demosec.entities.Notification;
import com.aziz.demosec.dto.NotificationDTO;
import com.aziz.demosec.repository.NotificationRepository;
import com.aziz.demosec.repository.MedicalEventRepository;
import com.aziz.demosec.repository.EventParticipationRepository;
import com.aziz.demosec.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final MedicalEventRepository medicalEventRepository;
    private final EventParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadForAdmin(String email) {
        return notificationRepository.findByRecipientEmailAndReadFalseOrderByCreatedAtDesc(email)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public void markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
    }

    @Override
    public void markAllAsRead(String email) {
        List<Notification> unread = notificationRepository.findByRecipientEmailAndReadFalseOrderByCreatedAtDesc(email);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public void clearAll(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            notificationRepository.deleteAllByRecipient(user);
        });
    }

    @Override
    public void sendNotification(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        NotificationDTO dto = toDTO(saved);
        
        // Send to specific user
        try {
            if (saved.getRecipient() != null) {
                String destination = "/queue/notifications";
                System.out.println("DEBUG: Sending WebSocket notification to user: " + saved.getRecipient().getEmail() + " on " + destination);
                messagingTemplate.convertAndSendToUser(
                    saved.getRecipient().getEmail(),
                    destination,
                    dto
                );
            }
        } catch (Exception e) {
            System.err.println("WebSocket delivery failed: " + e.getMessage());
        }
    }

    private NotificationDTO toDTO(Notification n) {
        NotificationDTO.NotificationDTOBuilder builder = NotificationDTO.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .targetId(n.getTargetId())
                .participationId(n.getParticipationId())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .senderName(n.getSender() != null ? n.getSender().getFullName() : "System");

        if (n.getTargetId() != null) {
            medicalEventRepository.findById(n.getTargetId()).ifPresent(event -> {
                builder.eventTitle(event.getTitle());
                builder.eventDate(event.getDate());
            });
        }

        if (n.getParticipationId() != null) {
            participationRepository.findById(n.getParticipationId()).ifPresent(p -> {
                builder.participationStatus(p.getStatus());
            });
        }

        return builder.build();
    }
}
