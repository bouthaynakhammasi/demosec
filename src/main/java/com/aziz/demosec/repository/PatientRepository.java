package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;



import com.aziz.demosec.dto.PatientSummaryDTO;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String email);

    boolean existsByEmail(String email);


    @Query("""
        SELECT new com.aziz.demosec.dto.PatientSummaryDTO(
            p.id, p.fullName,
            c.date,
            d.description,
            pi.medicationName,
            lp.title
        )
        FROM Consultation c
        JOIN c.medicalRecord mr
        JOIN mr.patient p
        LEFT JOIN Diagnosis d ON d.consultation = c
        LEFT JOIN Prescription pr ON pr.consultation = c
        LEFT JOIN pr.items pi
        LEFT JOIN LifestyleGoal lg ON lg.patient = p
        LEFT JOIN lg.plans lp
        WHERE c.date = (
            SELECT MAX(c2.date)
            FROM Consultation c2
            WHERE c2.medicalRecord.patient = p
        )
    """)
    List<PatientSummaryDTO> getPatientSummaries();

}
