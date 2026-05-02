package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.EmergencyAlert;
import com.aziz.demosec.Entities.EmergencyAlertStatus;
import com.aziz.demosec.Entities.EmergencySeverity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, Long> {
    List<EmergencyAlert> findByStatus(EmergencyAlertStatus status);
    List<EmergencyAlert> findBySeverity(EmergencySeverity severity);
    List<EmergencyAlert> findByDeviceId(Long deviceId);
    List<EmergencyAlert> findByCanceledByPatient(Boolean canceled);
}
