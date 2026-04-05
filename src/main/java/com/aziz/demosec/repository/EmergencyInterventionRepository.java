package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.EmergencyIntervention;
import com.aziz.demosec.Entities.EmergencyInterventionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmergencyInterventionRepository extends JpaRepository<EmergencyIntervention, Long> {
    Optional<EmergencyIntervention> findByEmergencyAlertId(Long alertId);
    List<EmergencyIntervention> findByClinicId(Long clinicId);
    List<EmergencyIntervention> findByStatus(EmergencyInterventionStatus status);
}
