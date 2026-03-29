package com.aziz.demosec.service;

import com.aziz.demosec.dto.pharmacy.DeliveryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Pushes real-time GPS updates to Angular clients via WebSocket.
 * Angular subscribes to: /topic/delivery/{orderId}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcast a delivery position update to all subscribers of this order.
     */
    public void broadcastDeliveryUpdate(Long orderId, DeliveryResponseDTO update) {
        String destination = "/topic/delivery/" + orderId;
        messagingTemplate.convertAndSend(destination, update);
        log.info("[WebSocket] Delivery update broadcast to {} — status: {}, lat: {}, lng: {}",
                destination, update.getStatus(), update.getCurrentLat(), update.getCurrentLng());
    }
}
