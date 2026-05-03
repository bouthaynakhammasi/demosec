package com.aziz.demosec.dto.homecare;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteRequestDTO {
    @Size(max = 1000, message = "Les notes du prestataire ne peuvent pas dépasser 1000 caractères")
    private String providerNotes;
}
