package com.aziz.demosec.controller;

import com.aziz.demosec.service.WsNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Temporary controller for testing WebSocket notifications.
 * Remove after confirming WebSocket works end-to-end.
 */
@RestController
@RequestMapping("/api/ws-test")
@RequiredArgsConstructor
public class WsTestController {

    private final WsNotificationService wsNotificationService;

    /** Test: send notification to admin topic */
    @PostMapping("/admin")
    public ResponseEntity<String> testAdmin() {
        wsNotificationService.notifyAdmin(
            "Test Admin 🔔",
            "WebSocket pipeline is working correctly.",
            "info"
        );
        return ResponseEntity.ok("Notification sent to /topic/admin/notifications");
    }

    /** Test: send notification to a specific patient */
    @PostMapping("/patient/{patientId}")
    public ResponseEntity<String> testPatient(@PathVariable Long patientId) {
        wsNotificationService.notifyPatient(
            patientId,
            "Test Patient 🔔",
            "WebSocket pipeline is working correctly.",
            "info"
        );
        return ResponseEntity.ok("Notification sent to /topic/patient/" + patientId + "/notifications");
    }
}
