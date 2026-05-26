package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.DeliveryAgent;
import com.aziz.demosec.Entities.DeliveryAgency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {
    List<DeliveryAgent> findByAgency(DeliveryAgency agency);
    List<DeliveryAgent> findByStatus(DeliveryAgent.AgentStatus status);
}
