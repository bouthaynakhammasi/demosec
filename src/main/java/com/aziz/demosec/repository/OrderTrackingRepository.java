package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {

    List<OrderTracking> findByOrder_IdOrderByChangedAtDesc(Long orderId);
}
