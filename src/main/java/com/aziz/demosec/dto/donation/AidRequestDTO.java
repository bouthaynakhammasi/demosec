package com.aziz.demosec.dto.donation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AidRequestDTO {
    private Long patientId;
    private String description;
    private String supportingDocument;
}
