package com.aziz.demosec.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResultRequest {

    @NotNull(message = "Lab request ID is required")
    @Positive(message = "Lab request ID must be positive")
    private Long labRequestId;

    @NotNull(message = "Lab test ID is required")
    @Positive(message = "Lab test ID must be positive")
    private Long labTestId;

    @NotNull(message = "Patient ID is required")
    @Positive(message = "Patient ID must be positive")
    private Long patientId;

    @NotNull(message = "Result date is required")
    @PastOrPresent(message = "Result date cannot be in the future")
    private LocalDateTime resultDate;

    @NotBlank(message = "Result file is required")
    @Size(min = 3, max = 255, message = "Result file must be between 3 and 255 characters")
    private String resultFile;

    @NotBlank(message = "Result data is required")
    @Size(min = 10, max = 5000, message = "Result data must be between 10 and 5000 characters")
    private String resultData;

    @NotNull(message = "Result status is required")
    private ResultStatus resultStatus;

    @Size(max = 100, message = "Test type must be maximum 100 characters")
    private String testType;

    @NotBlank(message = "Normal value is required")
    @Size(min = 1, max = 200, message = "Normal value must be between 1 and 200 characters")
    private String normalValue;

    @Size(max = 500, message = "Abnormal findings must be maximum 500 characters")
    private String abnormalFindings;

    @NotBlank(message = "Technician name is required")
    @Size(min = 2, max = 100, message = "Technician name must be between 2 and 100 characters")
    private String technicianName;

    @NotNull(message = "Technician ID is required")
    @Positive(message = "Technician ID must be positive")
    private Long technicianId;

    private String verifiedBy;

    @Builder.Default
    private Boolean isAbnormal = false;

    @Size(max = 1000, message = "Recommendations must not exceed 1000 characters")
    private String recommendations;

    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 5, message = "Priority must be at most 5")
    private Integer priority;

    @Builder.Default
    private Boolean requiresFollowUp = false;

    @Size(max = 500, message = "Follow-up instructions must not exceed 500 characters")
    private String followUpInstructions;

    private String unit;

    @DecimalMin(value = "0.001", message = "Minimum value must be positive")
    @DecimalMax(value = "999999.999", message = "Minimum value is too large")
    private BigDecimal minValue;

    @DecimalMin(value = "0.001", message = "Maximum value must be positive")
    @DecimalMax(value = "999999.999", message = "Maximum value is too large")
    private BigDecimal maxValue;

    @Size(max = 200, message = "Result category must not exceed 200 characters")
    private String resultCategory;

    @Builder.Default
    private Boolean isUrgent = false;

    @Min(value = 0, message = "Confidence level cannot be negative")
    @Max(value = 100, message = "Confidence level cannot exceed 100")
    private Integer confidenceLevel;

    @AssertTrue(message = "Minimum value must be less than or equal to maximum value")
    private boolean isValueRangeValid() {
        if (minValue == null || maxValue == null) {
            return true;
        }
        return minValue.compareTo(maxValue) <= 0;
    }

    public enum ResultStatus {
        PENDING,
        COMPLETED,
        ABNORMAL,
        CRITICAL,
        CANCELLED,
        REVIEW_REQUIRED
    }
}