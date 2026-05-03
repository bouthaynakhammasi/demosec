package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    List<Diagnosis> findByConsultationMedicalRecordId(Long recordId);
    List<Diagnosis> findByConsultationId(Long consultationId);
}