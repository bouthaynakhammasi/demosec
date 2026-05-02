package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Prescription;
import com.aziz.demosec.Entities.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    java.util.List<Prescription> findByConsultationMedicalRecordId(Long medicalRecordId);
    // ── Used by the scheduler ─────────────────────────────────────────────

    /**
     * Keyword: all ACTIVE prescriptions whose computed expiryDate is before today.
     * Spans: Prescription (status + expiryDate).
     */
    List<Prescription> findByStatusAndExpiryDateBefore(PrescriptionStatus status, LocalDate date);

    /**
     * Keyword: all ACTIVE prescriptions that have NO expiryDate yet
     * (first run after migration — needs duration parsing).
     */
    List<Prescription> findByStatusAndExpiryDateIsNull(PrescriptionStatus status);

    // ── Useful for the dashboard / API ────────────────────────────────────

    /**
     * JPQL: find all prescriptions for a given patient (by username),
     * joining Prescription → Consultation → MedicalRecord → Patient.
     */
    @Query("""
            SELECT p
            FROM Prescription p
            JOIN p.consultation c
            JOIN c.medicalRecord mr
            JOIN mr.patient      pat
            WHERE pat.fullName = :fullName
            ORDER BY p.date DESC
            """)
    List<Prescription> findAllByPatientFullName(@Param("fullName") String fullName);

    /**
     * JPQL: count expired prescriptions per doctor in a date window.
     * Used by the workload report.
     */
    @Query("""
            SELECT COUNT(p)
            FROM Prescription p
            JOIN p.consultation c
            JOIN c.doctor doc
            WHERE doc.fullName = :doctorFullName
              AND p.status     = com.aziz.demosec.Entities.PrescriptionStatus.EXPIRED
              AND p.date BETWEEN :from AND :to
            """)
    long countExpiredByDoctorFullName(
            @Param("doctorFullName") String doctorFullName,
            @Param("from") LocalDate from,
            @Param("to")   LocalDate to
    );
}