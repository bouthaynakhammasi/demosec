package com.aziz.demosec.dto;

import lombok.Data;

@Data
public class AppointmentDTO {
    private Long id;
    private String date;
    private String startTime;
    private String endTime;
    private String status;
    private String mode;
    private String notes;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private String doctorProfilePicture;
    private String clinicName;
    private String clinicAddress;
    private String patientName;
    private String meetingLink;
}
