package com.aziz.demosec.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString
public class AppointmentRequest {
    private Long patientId;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    private Long providerId;
    
    @NotBlank(message = "Date is required")
    private String date;
    
    @NotBlank(message = "Start time is required")
    private String startTime;
    
    @NotBlank(message = "End time is required")
    private String endTime;
    
    @NotBlank(message = "Mode is required (ONLINE or IN_PERSON)")
    private String mode;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
