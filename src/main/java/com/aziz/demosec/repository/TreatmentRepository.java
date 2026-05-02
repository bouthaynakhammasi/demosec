package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Treatment;
import com.aziz.demosec.Entities.TreatmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    java.util.List<Treatment> findByConsultationMedicalRecordId(Long medicalRecordId);
    // Used by the scheduler — find treatments that expired while still active
    List<Treatment> findByStatusAndEndDateBefore(
            TreatmentStatus status,
            LocalDate date
    );

    // Used by the scheduler — orphaned records: startDate is after today but endDate already passed
    List<Treatment> findByStatusAndStartDateAfterAndEndDateBefore(
            TreatmentStatus status,
            LocalDate startAfter,
            LocalDate endBefore
    );
}