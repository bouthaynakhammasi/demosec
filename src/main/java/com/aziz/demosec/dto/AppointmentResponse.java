package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.AppointmentStatus;
import com.aziz.demosec.Entities.AvailabilityMode;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;

    private Long patientId;
    private Long providerId;
    private Long availabilityId;

    private AppointmentStatus status;

    private AvailabilityMode mode;
    private String meetingLink;
    private String visitAddress;
}