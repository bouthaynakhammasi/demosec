package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.Notification;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.repository.NotificationRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications() {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(notificationRepository.findByRecipientOrderByCreatedAtDesc(user));
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }
}
