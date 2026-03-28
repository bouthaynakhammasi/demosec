package com.aziz.demosec.dto.homecare;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateServiceRequestDTO {
    private Long serviceId;
    private Long providerId;          // optionnel — patient peut choisir son prestataire
    private LocalDateTime requestedDateTime;
    private String address;
    private String patientNotes;
}
