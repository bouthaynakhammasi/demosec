package com.aziz.demosec.dto.baby;

import com.aziz.demosec.Entities.JournalEntryType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class JournalEntryRequestDTO {
    @NotNull(message = "Le type de journal est obligatoire")
    private JournalEntryType type;

    @NotBlank(message = "La valeur du journal ne peut pas être vide")
    private String value;

    @Size(max = 500, message = "Les notes ne peuvent dépasser 500 caractères")
    private String notes;

    private String metadata;
}
