package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
}
=======

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    java.util.Optional<MedicalRecord> findByPatientId(Long patientId);
}
>>>>>>> origin/MedicalRecord
