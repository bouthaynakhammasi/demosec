package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.DeliveryAgency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryAgencyRepository extends JpaRepository<DeliveryAgency, Long> {
}
