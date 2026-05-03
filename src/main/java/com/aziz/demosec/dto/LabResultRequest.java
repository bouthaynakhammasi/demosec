package com.aziz.demosec.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LabResultRequest {

    @NotNull(message = "Lab request ID is required")
    @Positive
    private Long labRequestId;

    @NotBlank(message = "Result data is required")
    private String resultData;

    @NotBlank(message = "Technician name is required")
    private String technicianName;

    @Builder.Default
    private Boolean isAbnormal = false;

    @Builder.Default
    private String status = "PENDING";

    private String abnormalFindings;
    private String verifiedBy;
}