package com.aziz.demosec.dto.pharmacy;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelOrderRequestDTO {
    @NotBlank(message = "Cancellation reason is required")
    private String reason;
}
