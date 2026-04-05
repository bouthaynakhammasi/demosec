package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.appointment.AppointmentStatus;
import com.aziz.demosec.Entities.appointment.Mode;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long providerId;
    private String providerName;
    private LocalDateTime dateTime;
    private String date;
    private String startTime;
    private String endTime;
    private Mode mode;
    private AppointmentStatus status;
    private String meetingLink;
    private String visitAddress;
    private String patientNotes;
    private String providerNotes;
    private String doctorSpecialty;
    private String clinicName;
    private String clinicAddress;
    private LocalDateTime cancelledAt;
    private LocalDateTime completedAt;
}
