package com.aziz.demosec.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.aziz.demosec.Entities.Payment;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket Service for Real-time Payment Notifications
 * Broadcasts payment status updates to connected clients via STOMP
 * 
 * Topics:
 * - /topic/payment/{patientId}/initiated - Payment process started
 * - /topic/payment/{patientId}/processing - Payment is being processed
 * - /topic/payment/{patientId}/completed - Payment successful
 * - /topic/payment/{patientId}/failed - Payment failed
 * - /topic/pharmacy/{pharmacyId}/payments - All payments for pharmacy
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Notify when payment is initiated
     */
    public void notifyPaymentInitiated(Payment payment) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "PAYMENT_INITIATED");
        payload.put("paymentId", payment.getId());
        payload.put("orderId", payment.getOrder().getId());
        payload.put("method", payment.getMethod().toString());
        payload.put("amount", payment.getAmount());
        payload.put("currency", payment.getCurrency());
        payload.put("timestamp", LocalDateTime.now());

        Long patientId = payment.getOrder().getPatient().getId();
        Long pharmacyId = payment.getOrder().getPharmacy().getId();

        messagingTemplate.convertAndSendToUser(
                patientId.toString(),
                "/queue/payments",
                payload);
        
        // Broadcast to pharmacy topic (staff members)
        messagingTemplate.convertAndSend(
                "/topic/pharmacy/" + pharmacyId + "/payments",
                payload);

        log.info("Payment initiated notification sent - Order: {}, Patient: {}",
                payment.getOrder().getId(), patientId);
    }

    /**
     * Notify when payment is processing (waiting for gateway response)
     */
    public void notifyPaymentProcessing(Payment payment) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "PAYMENT_PROCESSING");
        payload.put("paymentId", payment.getId());
        payload.put("orderId", payment.getOrder().getId());
        payload.put("status", payment.getStatus().toString());
        payload.put("timestamp", LocalDateTime.now());

        Long patientId = payment.getOrder().getPatient().getId();
        Long pharmacyId = payment.getOrder().getPharmacy().getId();

        messagingTemplate.convertAndSendToUser(
                patientId.toString(),
                "/queue/payments",
                payload);
        
        messagingTemplate.convertAndSend(
                "/topic/pharmacy/" + pharmacyId + "/payments",
                payload);

        log.info("Payment processing notification sent - Order: {}",
                payment.getOrder().getId());
    }

    /**
     * Notify when payment is successfully completed
     */
    public void notifyPaymentCompleted(Payment payment) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "PAYMENT_COMPLETED");
        payload.put("paymentId", payment.getId());
        payload.put("orderId", payment.getOrder().getId());
        payload.put("transactionId", payment.getTransactionId());
        payload.put("method", payment.getMethod().toString());
        payload.put("amount", payment.getAmount());
        payload.put("status", payment.getStatus().toString());
        payload.put("confirmedAt", payment.getConfirmedAt());
        payload.put("timestamp", LocalDateTime.now());

        Long patientId = payment.getOrder().getPatient().getId();
        Long pharmacyId = payment.getOrder().getPharmacy().getId();

        messagingTemplate.convertAndSendToUser(
                patientId.toString(),
                "/queue/payments",
                payload);
        
        messagingTemplate.convertAndSend(
                "/topic/pharmacy/" + pharmacyId + "/payments",
                payload);

        // Also notify pharmacy about order readiness
        messagingTemplate.convertAndSend(
                "/topic/pharmacy/" + pharmacyId + "/orders/ready",
                Map.of(
                        "event", "ORDER_PAYMENT_CONFIRMED",
                        "orderId", payment.getOrder().getId(),
                        "timestamp", LocalDateTime.now()));

        log.info("Payment completed notification sent - Order: {}, TxnId: {}",
                payment.getOrder().getId(), payment.getTransactionId());
    }

    /**
     * Notify when payment fails
     */
    public void notifyPaymentFailed(Payment payment, String reason) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "PAYMENT_FAILED");
        payload.put("paymentId", payment.getId());
        payload.put("orderId", payment.getOrder().getId());
        payload.put("method", payment.getMethod().toString());
        payload.put("amount", payment.getAmount());
        payload.put("reason", reason);
        payload.put("status", payment.getStatus().toString());
        payload.put("timestamp", LocalDateTime.now());

        Long patientId = payment.getOrder().getPatient().getId();
        Long pharmacyId = payment.getOrder().getPharmacy().getId();

        messagingTemplate.convertAndSendToUser(
                patientId.toString(),
                "/queue/payments",
                payload);
        
        messagingTemplate.convertAndSend(
                "/topic/pharmacy/" + pharmacyId + "/payments",
                payload);

        // Alert pharmacy staff
        messagingTemplate.convertAndSend(
                "/topic/pharmacy/" + pharmacyId + "/alerts",
                Map.of(
                        "event", "PAYMENT_FAILURE",
                        "orderId", payment.getOrder().getId(),
                        "reason", reason,
                        "timestamp", LocalDateTime.now()));

        log.warn("Payment failed notification sent - Order: {}, Reason: {}",
                payment.getOrder().getId(), reason);
    }

    /**
     * Notify when payment is refunded
     */
    public void notifyPaymentRefunded(Payment payment, String refundId, String reason) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "PAYMENT_REFUNDED");
        payload.put("paymentId", payment.getId());
        payload.put("refundId", refundId);
        payload.put("orderId", payment.getOrder().getId());
        payload.put("amount", payment.getAmount());
        payload.put("reason", reason);
        payload.put("timestamp", LocalDateTime.now());

        Long patientId = payment.getOrder().getPatient().getId();
        Long pharmacyId = payment.getOrder().getPharmacy().getId();

        messagingTemplate.convertAndSendToUser(
                patientId.toString(),
                "/queue/payments",
                payload);
        
        messagingTemplate.convertAndSend(
                "/topic/pharmacy/" + pharmacyId + "/payments",
                payload);

        log.info("Payment refunded notification sent - Order: {}, RefundId: {}",
                payment.getOrder().getId(), refundId);
    }

    /**
     * Broadcast payment status to a specific pharmacy
     */
    public void broadcastToPharmacy(Long pharmacyId, String eventType, Map<String, Object> data) {
        Map<String, Object> payload = new HashMap<>(data);
        payload.put("event", eventType);
        payload.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSend(
                "/topic/pharmacy/" + pharmacyId + "/payments",
                payload);

        log.debug("Broadcast to pharmacy {} - Event: {}", pharmacyId, eventType);
    }

    /**
     * Broadcast payment status to a specific patient
     */
    public void broadcastToPatient(Long patientId, String eventType, Map<String, Object> data) {
        Map<String, Object> payload = new HashMap<>(data);
        payload.put("event", eventType);
        payload.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
                patientId.toString(),
                "/queue/payments",
                payload);

        log.debug("Broadcast to patient {} - Event: {}", patientId, eventType);
    }
}
