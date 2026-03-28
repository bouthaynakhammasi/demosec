package com.aziz.demosec.dto.pharmacy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionNoteRequestDTO {

    @NotNull
    private Long orderId;
    @NotBlank
    private String medicationName;
    @NotBlank
    private String comment;
    private String duration;
    private String dosage;
    private Long pharmacistId;
}
