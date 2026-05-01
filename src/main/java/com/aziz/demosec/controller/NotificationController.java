package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.Notification;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.NotificationDto;
import com.aziz.demosec.repository.NotificationRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // ── REST ────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getMyNotifications() {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).build();

        List<NotificationDto> dtos = notificationRepository
            .findByRecipientOrderByCreatedAtDesc(user)
            .stream()
            .map(n -> NotificationDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .relatedId(n.getRelatedId())
                .build())
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.ok(0L);
        return ResponseEntity.ok(notificationRepository.countByRecipientAndIsReadFalse(user));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllRead() {
        User user = getCurrentUser();
        if (user != null) {
            List<Notification> unread = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
            unread.forEach(n -> n.setRead(true));
            notificationRepository.saveAll(unread);
        }
        return ResponseEntity.ok().build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }
}
