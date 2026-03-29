package com.aziz.demosec.dto.emergency;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyInterventionRequestDTO {

    @NotNull(message = "L'ID de l'alerte d'urgence est obligatoire")
    @Positive(message = "L'ID de l'alerte d'urgence doit être un nombre positif")
    private Long emergencyAlertId;

    @NotNull(message = "L'ID de la clinique est obligatoire")
    @Positive(message = "L'ID de la clinique doit être un nombre positif")
    private Long clinicId;

    @NotNull(message = "L'ID de l'ambulance est obligatoire")
    @Positive(message = "L'ID de l'ambulance doit être un nombre positif")
    private Long ambulanceId;
}
