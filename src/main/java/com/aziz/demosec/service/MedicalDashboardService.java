package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.Entities.TreatmentStatus;
import com.aziz.demosec.dto.DoctorWorkloadDTO;
import com.aziz.demosec.dto.PatientHistoryDTO;
import com.aziz.demosec.repository.ConsultationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalDashboardService {

    private final ConsultationRepository consultationRepository;

    /**
     * JPQL (multi-join): Full patient history.
     * Returns one row per (consultation × diagnosis × treatment) for a patient.
     */
    public List<PatientHistoryDTO> getPatientHistory(String patientFullName) {
        return consultationRepository.findFullPatientHistory(patientFullName);
    }

    /**
     * Keyword-based (multi-table): Doctor's active consultations in a date window
     * that still have at least one IN_PROGRESS treatment.
     */
    public List<Consultation> getDoctorActiveConsultations(
            String doctorFullName,
            LocalDateTime from,
            LocalDateTime to) {

        return consultationRepository
                .findByDoctorFullNameAndDateBetweenAndTreatments_Status(
                        doctorFullName, from, to, TreatmentStatus.ONGOING);
    }

    /**
     * JPQL aggregation: Doctor workload report across a date range.
     * Shows #active consultations and #pending prescriptions per doctor.
     */
    public List<DoctorWorkloadDTO> getDoctorWorkload(LocalDateTime from, LocalDateTime to) {
        return consultationRepository.findDoctorWorkload(from, to);
    }
}