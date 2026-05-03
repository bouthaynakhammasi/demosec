package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.dto.DoctorWorkloadDTO;
import com.aziz.demosec.dto.PatientHistoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    java.util.List<Consultation> findByMedicalRecordId(Long medicalRecordId);
    // =========================================================================
    // 1. COMPLEX JPQL QUERY (explicit joins across 5 tables)
    //    Returns the full patient history: record → consultation → diagnosis +
    //    treatment, filtered by patient username.
    // =========================================================================
    @Query("""
            SELECT new com.aziz.demosec.dto.PatientHistoryDTO(
                mr.id,
                p.fullName,
                p.email,
                c.id,
                c.date,
                doc.fullName,
                c.observations,
                c.notes,
                d.id,
                d.description,
                d.type,
                t.id,
                t.treatmentType,
                t.description,
                t.startDate,
                t.endDate,
                t.status
            )
            FROM Consultation c
            JOIN c.medicalRecord mr
            JOIN mr.patient      p
            JOIN c.doctor        doc
            JOIN Diagnosis d ON d.consultation = c
            JOIN Treatment t ON t.consultation = c
            WHERE p.fullName = :fullName
            ORDER BY c.date DESC
            """)
    List<PatientHistoryDTO> findFullPatientHistory(@Param("fullName") String fullName);


    // =========================================================================
    // 2. COMPLEX KEYWORD-BASED QUERY (Spring Data derived method)
    //    Finds all consultations for a specific doctor that occurred within a
    //    date range AND have at least one treatment with a given status.
    //    Spans: Consultation ↔ User (doctor) ↔ Treatment
    // =========================================================================
    List<Consultation> findByDoctorFullNameAndDateBetweenAndTreatments_Status(
            String doctorFullName,
            LocalDateTime from,
            LocalDateTime to,
            com.aziz.demosec.Entities.TreatmentStatus treatmentStatus
    );


    // =========================================================================
    // 3. JPQL — Doctor Workload Aggregation
    //    Counts active consultations (those with IN_PROGRESS treatments) and
    //    pending prescriptions per doctor, within a date window.
    //    Joins: Consultation → User(doctor) → Treatment → Prescription
    // =========================================================================
    @Query("""
            SELECT new com.aziz.demosec.dto.DoctorWorkloadDTO(
                doc.id,
                doc.fullName,
                doc.email,
                COUNT(DISTINCT c.id),
                COUNT(DISTINCT pr.id)
            )
            FROM Consultation c
            JOIN c.doctor        doc
            JOIN Treatment  t  ON t.consultation = c
            JOIN Prescription pr ON pr.consultation = c
            WHERE t.status     = com.aziz.demosec.Entities.TreatmentStatus.ONGOING
              AND c.date BETWEEN :from AND :to
            GROUP BY doc.id, doc.fullName, doc.email
            ORDER BY COUNT(DISTINCT c.id) DESC
            """)
    List<DoctorWorkloadDTO> findDoctorWorkload(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );
}