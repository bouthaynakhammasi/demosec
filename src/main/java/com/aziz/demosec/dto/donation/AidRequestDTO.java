package com.aziz.demosec.dto.donation;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AidRequestDTO {
    
    @NotNull(message = "L'ID du patient est obligatoire")
    private Long patientId;
    
    @NotBlank(message = "La description de la demande d'aide est requise")
    @Size(min = 10, max = 500, message = "La description doit comporter entre 10 et 500 caractères")
    private String description;
    
    private String supportingDocument;
}
