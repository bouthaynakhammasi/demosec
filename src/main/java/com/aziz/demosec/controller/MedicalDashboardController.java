package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.dto.DoctorWorkloadDTO;
import com.aziz.demosec.dto.PatientHistoryDTO;
import com.aziz.demosec.service.MedicalDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class MedicalDashboardController {

    private final MedicalDashboardService dashboardService;

    /**
     * GET /api/dashboard/patient-history?fullName=john
     * JPQL multi-join: full patient history (consultations + diagnoses + treatments).
     */
    @GetMapping("/patient-history")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<List<PatientHistoryDTO>> getPatientHistory(
            @RequestParam String fullName) {

        return ResponseEntity.ok(dashboardService.getPatientHistory(fullName));
    }

    /**
     * GET /api/dashboard/doctor-active-consultations
     *       ?doctorFullName=drsmith&from=2024-01-01T00:00:00&to=2024-12-31T23:59:59
     * Keyword-based: consultations for a doctor in a date range that have IN_PROGRESS treatments.
     */
    @GetMapping("/doctor-active-consultations")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<List<Consultation>> getDoctorActiveConsultations(
            @RequestParam String doctorFullName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        return ResponseEntity.ok(
                dashboardService.getDoctorActiveConsultations(doctorFullName, from, to));
    }

    /**
     * GET /api/dashboard/workload?from=2024-01-01T00:00:00&to=2024-12-31T23:59:59
     * JPQL aggregation: doctor workload report.
     */
    @GetMapping("/workload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DoctorWorkloadDTO>> getDoctorWorkload(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        return ResponseEntity.ok(dashboardService.getDoctorWorkload(from, to));
    }
}