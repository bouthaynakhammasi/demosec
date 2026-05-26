package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.dto.pharmacy.NotificationResponseDTO;
import com.aziz.demosec.repository.NotificationRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import java.time.LocalDateTime;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getForUser(Long userId) {
        return notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnread(Long userId) {
        return notificationRepository.findByRecipient_IdAndIsReadFalse(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByRecipient_IdAndIsReadFalse(userId);
    }

    @Override
    public NotificationResponseDTO markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        n.setRead(true);
        return toDTO(notificationRepository.save(n));
    }

    @Override
    public void notifyAdminsOfNewPharmacist(Pharmacist pharmacist) {
        String title = "Nouveau Pharmacien : " + pharmacist.getFullName();
        String message = "Une nouvelle demande d'inscription pour la pharmacie " + 
                         (pharmacist.getPharmacy() != null ? pharmacist.getPharmacy().getName() : "N/A") + 
                         " est en attente de validation.";

        userRepository.findByRole(Role.ADMIN).forEach(admin -> {
            Notification n = Notification.builder()
                    .recipient(admin)
                    .title(title)
                    .message(message)
                    .type(NotificationType.REG_REQ)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            Notification saved = notificationRepository.save(n);
            
            // Send real-time WebSocket notification
            messagingTemplate.convertAndSendToUser(
                admin.getEmail(),
                "/queue/notifications",
                toDTO(saved)
            );
        });
    }

    @Override
    public void notifyAdminsOfNewProvider(ServiceProvider provider) {
        String title = "Nouveau Prestataire : " + provider.getFullName();
        String message = "Une nouvelle demande d'inscription pour un prestataire de soins à domicile est en attente de validation.";

        userRepository.findByRole(Role.ADMIN).forEach(admin -> {
            Notification n = Notification.builder()
                    .recipient(admin)
                    .title(title)
                    .message(message)
                    .type(NotificationType.REG_REQ)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            Notification saved = notificationRepository.save(n);
            
            // Send real-time WebSocket notification
            messagingTemplate.convertAndSendToUser(
                admin.getEmail(),
                "/queue/notifications",
                toDTO(saved)
            );
        });
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.findByRecipient_IdAndIsReadFalse(userId).forEach(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Override
    public void notifyAccountActivated(User user) {
        Notification n = Notification.builder()
                .recipient(user)
                .title("Compte Activé")
                .message("Votre compte a été validé par un administrateur. Vous pouvez maintenant vous connecter.")
                .type(NotificationType.ACCOUNT_ACTIVATED)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        Notification saved = notificationRepository.save(n);
        
        // Send real-time WebSocket notification
        messagingTemplate.convertAndSendToUser(
            user.getEmail(),
            "/queue/notifications",
            toDTO(saved)
        );
    }

    @Override
    public void notifyDeliveryCreated(Delivery delivery) {
        Notification n = Notification.builder()
                .recipient(delivery.getOrder().getPatient())
                .order(delivery.getOrder())
                .title("Commande Expédiée")
                .message("Votre commande #" + delivery.getOrder().getId() + " a été expédiée. Numéro de suivi : " + delivery.getTrackingNumber())
                .type(NotificationType.DELIVERY_ASSIGNED)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        Notification saved = notificationRepository.save(n);
        
        messagingTemplate.convertAndSendToUser(
            delivery.getOrder().getPatient().getEmail(),
            "/queue/notifications",
            toDTO(saved)
        );
    }

    @Override
    public void notifyDeliveryStatusUpdate(Delivery delivery) {
        String message;
        NotificationType type;
        
        switch (delivery.getStatus()) {
            case PICKED_UP -> {
                message = "Votre commande est en cours de ramassage.";
                type = NotificationType.DELIVERY_PICKED_UP;
            }
            case IN_TRANSIT -> {
                message = "Votre commande est en route.";
                type = NotificationType.OUT_FOR_DELIVERY;
            }
            case DELIVERED -> {
                message = "Votre commande a été livrée avec succès.";
                type = NotificationType.DELIVERED;
            }
            default -> {
                message = "Le statut de votre livraison a été mis à jour : " + delivery.getStatus();
                type = NotificationType.DELIVERY_ASSIGNED;
            }
        }

        Notification n = Notification.builder()
                .recipient(delivery.getOrder().getPatient())
                .order(delivery.getOrder())
                .title("Mise à jour Livraison")
                .message(message)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        Notification saved = notificationRepository.save(n);
        
        messagingTemplate.convertAndSendToUser(
            delivery.getOrder().getPatient().getEmail(),
            "/queue/notifications",
            toDTO(saved)
        );
    }

    private NotificationResponseDTO toDTO(Notification n) {
        return NotificationResponseDTO.builder()
                .id(n.getId())
                .recipientId(n.getRecipient() != null ? n.getRecipient().getId() : null)
                .orderId(n.getOrder() != null ? n.getOrder().getId() : null)
                .title(n.getTitle()).message(n.getMessage())
                .type(n.getType()).isRead(n.isRead()).createdAt(n.getCreatedAt())
                .build();
    }
}
