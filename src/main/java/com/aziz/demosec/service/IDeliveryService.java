package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Delivery;
import com.aziz.demosec.Entities.DeliveryAgency;
import com.aziz.demosec.Entities.DeliveryAgent;
import com.aziz.demosec.Entities.DeliveryStatus;
import java.util.List;

import java.util.Optional;

import com.aziz.demosec.dto.pharmacy.DispatchOrderRequest;

public interface IDeliveryService {
    Delivery createDelivery(Long orderId);
    Delivery createDelivery(DispatchOrderRequest request);
    Delivery updateDeliveryStatus(Long deliveryId, DeliveryStatus status);
    Optional<Delivery> getDeliveryByOrderId(Long orderId);
    List<Delivery> getDeliveriesByAgent(Long agentId);
    Delivery getDeliveryByTrackingNumber(String trackingNumber);
    Delivery updateLocation(Long deliveryId, Double lat, Double lng);
    
    // Advanced Simulation Methods
    List<DeliveryAgency> getAllAgencies();
    List<DeliveryAgent> getAgentsByAgency(Long agencyId);
    Delivery assignAgent(Long deliveryId, Long agentId);
}
