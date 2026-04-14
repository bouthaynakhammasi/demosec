package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Long orderId);
    Optional<Delivery> findByTrackingNumber(String trackingNumber);
    List<Delivery> findByAgentId(Long agentId);
}
