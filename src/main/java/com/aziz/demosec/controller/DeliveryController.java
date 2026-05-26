package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.DeliveryAgency;
import com.aziz.demosec.Entities.DeliveryAgent;
import com.aziz.demosec.Entities.Delivery;
import com.aziz.demosec.Entities.DeliveryStatus;
import com.aziz.demosec.service.IDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final IDeliveryService deliveryService;

    @PostMapping("/dispatch")
    public ResponseEntity<Delivery> dispatchOrder(@RequestBody com.aziz.demosec.dto.pharmacy.DispatchOrderRequest request) {
        return ResponseEntity.ok(deliveryService.createDelivery(request));
    }

    @PatchMapping("/{deliveryId}/status")
    public ResponseEntity<Delivery> updateStatus(@PathVariable("deliveryId") Long deliveryId, @RequestParam("status") DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(deliveryId, status));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Delivery> getByOrderId(@PathVariable("orderId") Long orderId) {
        return deliveryService.getDeliveryByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Delivery> getByTrackingNumber(@PathVariable("trackingNumber") String trackingNumber) {
        return ResponseEntity.ok(deliveryService.getDeliveryByTrackingNumber(trackingNumber));
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Delivery>> getDeliveriesByAgent(@PathVariable("agentId") Long agentId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByAgent(agentId));
    }

    @GetMapping("/agencies")
    public ResponseEntity<List<DeliveryAgency>> getAllAgencies() {
        return ResponseEntity.ok(deliveryService.getAllAgencies());
    }

    @GetMapping("/agencies/{agencyId}/agents")
    public ResponseEntity<List<DeliveryAgent>> getAgentsByAgency(@PathVariable("agencyId") Long agencyId) {
        return ResponseEntity.ok(deliveryService.getAgentsByAgency(agencyId));
    }

    @PostMapping("/{deliveryId}/assign/{agentId}")
    public ResponseEntity<Delivery> assignAgent(@PathVariable("deliveryId") Long deliveryId, @PathVariable("agentId") Long agentId) {
        return ResponseEntity.ok(deliveryService.assignAgent(deliveryId, agentId));
    }
}
