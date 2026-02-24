package com.aziz.demosec.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PrescriptionResponse {

    private Long id;
    private Long consultationId;
    private LocalDate date;

    private List<PrescriptionItemResponse> items;
}