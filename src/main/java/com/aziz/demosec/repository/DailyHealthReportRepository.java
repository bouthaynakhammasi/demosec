package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.DailyHealthReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyHealthReportRepository extends JpaRepository<DailyHealthReport, Long> {

    List<DailyHealthReport> findByPatientId(Long patientId);
    List<DailyHealthReport> findByPatientIdAndReportDateBetween(Long patientId, LocalDate from, LocalDate to);
    Optional<DailyHealthReport> findByPatientIdAndReportDate(Long patientId, LocalDate date);
    List<DailyHealthReport> findByAnomalyDetectedTrue();
    List<DailyHealthReport> findByLifestylePlanId(Long lifestylePlanId);

    @Query("SELECT r FROM DailyHealthReport r WHERE r.patient.id IN " +
           "(SELECT DISTINCT a.patient.id FROM Appointment a WHERE a.provider.id = :nutritionistId) " +
           "AND r.reportDate = (SELECT MAX(r2.reportDate) FROM DailyHealthReport r2 WHERE r2.patient = r.patient)")
    List<DailyHealthReport> findLatestReportsByNutritionist(@Param("nutritionistId") Long nutritionistId);
}