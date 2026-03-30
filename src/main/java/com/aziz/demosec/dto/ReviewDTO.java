package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long doctorId;
    private String patientName;
    private Long patientId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private Boolean isAnonymous;
}
