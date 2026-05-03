package com.aziz.demosec.controller;

import com.aziz.demosec.dto.NotificationDTO;
import com.aziz.demosec.dto.pharmacy.NotificationResponseDTO;
import com.aziz.demosec.service.INotificationService;
import com.aziz.demosec.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;
    private final NotificationService notificationServicePharmacy;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'PHARMACIST')")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getUnreadForAdmin(userDetails.getUsername()));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'PHARMACIST')")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'PHARMACIST')")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAllAsRead(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'PHARMACIST')")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/clear-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'PHARMACIST')")
    public ResponseEntity<?> clearAll(@AuthenticationPrincipal UserDetails userDetails) {
        notificationService.clearAll(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getForUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(notificationServicePharmacy.getForUser(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnread(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(notificationServicePharmacy.getUnread(userId));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countUnread(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(Map.of("unreadCount", notificationServicePharmacy.countUnread(userId)));
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsReadByUserId(@PathVariable("userId") Long userId) {
        notificationServicePharmacy.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}