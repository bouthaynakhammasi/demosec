package com.aziz.demosec.dto.homecare;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class CreateServiceRequestDTO {
    private Long serviceId;
    private Long providerId;// optionnel — patient peut choisir son prestataire
    @NotNull(message = "La date de l'intervention est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime requestedDateTime;
    @NotNull(message = "L'adresse est obligatoire")
    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
    private String address;
    @NotBlank(message = "Les notes du patient sont obligatoires")
    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    private String patientNotes;
}
