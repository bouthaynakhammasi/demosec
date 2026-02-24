package com.aziz.demosec.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResultRequest {

    @NotNull(message = "labRequestId is required")
    private Long labRequestId;

    private String resultFile;

    private String resultData;
}