package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.AidRequest;
import com.aziz.demosec.Entities.AidRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AidRequestRepository extends JpaRepository<AidRequest, Long> {
    List<AidRequest> findByPatientId(Long patientId);
    List<AidRequest> findByStatus(AidRequestStatus status);
}
