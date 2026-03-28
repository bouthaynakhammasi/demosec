package com.aziz.demosec.dto.pharmacy;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejectOrderRequestDTO {
    @NotBlank(message = "Rejection note is required")
    private String note;
    private String changedBy;
}
