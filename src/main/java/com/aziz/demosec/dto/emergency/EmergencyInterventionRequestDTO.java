package com.aziz.demosec.dto.emergency;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyInterventionRequestDTO {
    private Long emergencyAlertId;
    private Long clinicId;
    private Long ambulanceId;
}
