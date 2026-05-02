package com.aziz.demosec.dto.emergency;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmartDeviceResponseDTO {
    private Long id;
    private Long patientId;
    private String patientName;
}
