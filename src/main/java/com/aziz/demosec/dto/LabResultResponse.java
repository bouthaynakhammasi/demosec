package com.aziz.demosec.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResultResponse {

    private Long id;
    private Long labRequestId;
    private String patientName;
    private String doctorName;
    private String laboratoryName;
    private String resultFile;
    private String resultData;
    private String testType;
    private String normalValue;
    private String abnormalFindings;
    private String technicianName;
    private String verifiedBy;
    private Boolean isAbnormal;
    private String recommendations;
    private Integer priority;
    private LocalDateTime completedAt;
    private LocalDateTime verifiedAt;
    private String status;
    private Boolean isUrgent;
    private String resultCategory;
}