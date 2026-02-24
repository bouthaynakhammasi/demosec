package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.LabRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabRequestRequest {

    @NotNull(message = "Patient id is required")
    private Long patientId;

    @NotNull(message = "Doctor id is required")
    private Long doctorId;

    @NotNull(message = "Laboratory id is required")
    private Long laboratoryId;

    private LabRequestStatus status;
}