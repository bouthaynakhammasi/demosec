package com.aziz.demosec.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabTestRequest {

    @NotBlank(message = "Test name is required")
    @Size(min = 2, max = 100, message = "Test name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "9999.99", message = "Price must be less than 10000")
    private BigDecimal price;

    @NotNull(message = "Laboratory ID is required")
    @Positive(message = "Laboratory ID must be positive")
    private Long laboratoryId;

    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    private String category;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 1440, message = "Duration must not exceed 1440 minutes (24 hours)")
    private Integer durationMinutes;

    @NotNull(message = "Test type is required")
    private TestType testType;

    @Builder.Default
    private Boolean requiresFasting = false;

    @Builder.Default
    private Boolean requiresAppointment = false;

    @Size(max = 1000, message = "Preparation instructions must not exceed 1000 characters")
    private String preparationInstructions;

    @Size(max = 1000, message = "Result interpretation must not exceed 1000 characters")
    private String resultInterpretation;

    @Min(value = 0, message = "Minimum age cannot be negative")
    @Max(value = 150, message = "Minimum age cannot exceed 150")
    private Integer minimumAge;

    @Min(value = 0, message = "Maximum age cannot be negative")
    @Max(value = 150, message = "Maximum age cannot exceed 150")
    private Integer maximumAge;

    private String genderSpecific;

    @AssertTrue(message = "Minimum age must be less than or equal to maximum age")
    private boolean isAgeRangeValid() {
        if (minimumAge == null || maximumAge == null) {
            return true;
        }
        return minimumAge <= maximumAge;
    }

    public enum TestType {
        BLOOD_TEST,
        URINE_TEST,
        IMAGING,
        BIOPSY,
        GENETIC,
        PATHOLOGY,
        OTHER
    }
}