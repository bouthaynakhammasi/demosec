package com.aziz.demosec.dto.emergency;

import com.aziz.demosec.Entities.EmergencySeverity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyAlertRequestDTO {
    private Long smartDeviceId;
    private EmergencySeverity severity;
    private Double latitude;
    private Double longitude;
}
