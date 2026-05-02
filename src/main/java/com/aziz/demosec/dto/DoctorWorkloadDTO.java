package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Returned by the keyword-based doctor-workload query.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorWorkloadDTO {
    private Long   doctorId;
    private String doctorFullName;
    private String doctorEmail;
    private long   activeConsultations;     // consultations with IN_PROGRESS treatments
    private long   pendingPrescriptions;    // prescriptions in those consultations
}