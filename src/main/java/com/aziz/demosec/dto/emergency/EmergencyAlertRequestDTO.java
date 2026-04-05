package com.aziz.demosec.dto.emergency;

import com.aziz.demosec.Entities.EmergencySeverity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyAlertRequestDTO {

    @NotNull(message = "L'ID du dispositif connecté est obligatoire")
    @Positive(message = "L'ID du dispositif doit être un nombre positif")
    private Long smartDeviceId;

    @NotNull(message = "La sévérité de l'urgence est obligatoire")
    private EmergencySeverity severity;

    @NotNull(message = "La latitude est obligatoire")
    @Min(value = -90, message = "La latitude doit être supérieure ou égale à -90")
    @Max(value = 90, message = "La latitude doit être inférieure ou égale à 90")
    private Double latitude;

    @NotNull(message = "La longitude est obligatoire")
    @Min(value = -180, message = "La longitude doit être supérieure ou égale à -180")
    @Max(value = 180, message = "La longitude doit être inférieure ou égale à 180")
    private Double longitude;
}
