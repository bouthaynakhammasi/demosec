package com.aziz.demosec.dto.emergency;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmbulanceRequestDTO {
    
    @NotNull(message = "L'ID de la clinique est obligatoire")
    private Long clinicId;
    
    private Double currentLat;
    
    private Double currentLng;
    
    @NotBlank(message = "La plaque d'immatriculation ne peut pas être vide")
    @Size(min = 2, max = 20, message = "La plaque d'immatriculation doit comporter entre 2 et 20 caractères")
    private String licensePlate;
    
    @Pattern(regexp = "^(AVAILABLE|ON_DUTY|EN_TRAVAIL)$", message = "Le statut doit être AVAILABLE, ON_DUTY, ou EN_TRAVAIL")
    private String status;
}
