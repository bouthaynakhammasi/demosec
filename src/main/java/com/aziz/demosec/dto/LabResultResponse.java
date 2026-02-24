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
    private String resultFile;
    private String resultData;
    private LocalDateTime completedAt;
}