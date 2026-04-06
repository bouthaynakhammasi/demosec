package com.aziz.demosec.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboratoryStaffRequest {

    @NotNull
    private Long laboratoryId;

    private String fullName;

    private String email;
}
