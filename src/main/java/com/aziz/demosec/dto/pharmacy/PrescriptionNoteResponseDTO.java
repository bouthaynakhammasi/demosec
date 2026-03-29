package com.aziz.demosec.dto.pharmacy;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionNoteResponseDTO {

    private Long id;
    private Long orderId;
    private String medicationName;
    private String comment;
    private String duration;
    private String dosage;
    private Long pharmacistId;
    private String pharmacistName;
}
