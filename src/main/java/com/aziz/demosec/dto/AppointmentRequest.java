package com.aziz.demosec.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString
public class AppointmentRequest {
    private Long patientId;
    private Long doctorId;
    private Long providerId;
    private String date;
    private String startTime;
    private String endTime;
    private String mode;
    private String notes;
}
