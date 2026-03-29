package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.TestType; // ✅ importer depuis Entities
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LabTestRequest {

    @NotBlank(message = "Test name is required")
    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "9999.99")
    private BigDecimal price;

    @NotNull(message = "Laboratory ID is required")
    @Positive
    private Long laboratoryId;

    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50)
    private String category;

    @NotNull(message = "Test type is required")
    private TestType testType; // ✅ Entities.TestType maintenant

    @Min(1)
    @Max(1440)
    private Integer durationMinutes;

    private String genderSpecific;

    @Builder.Default
    private Boolean requiresAppointment = false;

    @Builder.Default
    private Boolean requiresFasting = false;

// ❌ SUPPRIMER l'enum interne Test
}