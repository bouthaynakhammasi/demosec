package com.aziz.demosec.dto.emergency;

import com.aziz.demosec.Entities.EmergencyInterventionStatus;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyInterventionResponseDTO {
    private Long id;
    private Long emergencyAlertId;
    private Long clinicId;
    private Long ambulanceId;
    private String patientName;
    private EmergencyInterventionStatus status;
    private LocalDateTime dispatchedAt;
    private LocalDateTime arrivedAt;
    private LocalDateTime completedAt;
}
