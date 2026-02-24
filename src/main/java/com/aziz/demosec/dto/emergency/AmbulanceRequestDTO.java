package com.aziz.demosec.dto.emergency;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmbulanceRequestDTO {
    private Long clinicId;
    private Double currentLat;
    private Double currentLng;
    private String licensePlate;
}
