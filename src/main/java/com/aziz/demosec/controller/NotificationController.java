package com.aziz.demosec.controller;

import com.aziz.demosec.dto.pharmacy.NotificationResponseDTO;
import com.aziz.demosec.service.INotificationService;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin("*")
public class NotificationController {

    private final INotificationService notificationService;
    private final UserRepository userRepository;

    // --- My Notifications (Authenticated User) ---
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications() {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(notificationService.getForUser(user.getId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getMyUnreadCount() {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.ok(Map.of("unreadCount", 0L));
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.countUnread(user.getId())));
    }

    // --- User Specific (Admin/System) ---
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getForUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(notificationService.getForUser(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnread(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(notificationService.getUnread(userId));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countUnread(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.countUnread(userId)));
    }

    // --- Status Updates ---
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable("id") Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable("userId") Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markMyAllRead() {
        User user = getCurrentUser();
        if (user != null) {
            notificationService.markAllAsRead(user.getId());
        }
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return null;
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }
}
