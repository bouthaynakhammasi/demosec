package com.aziz.demosec.dto.pharmacy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejectOrderRequestDTO {
    @NotBlank(message = "Rejection note is required")
    @Size(max = 1000, message = "Rejection note must not exceed 1000 characters")
    private String note;
    private String changedBy;
}
