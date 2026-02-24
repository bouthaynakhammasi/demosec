package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.LabRequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabRequestResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Long laboratoryId;
    private String laboratoryName;
    private LabRequestStatus status;
    private LocalDateTime requestedAt;
}