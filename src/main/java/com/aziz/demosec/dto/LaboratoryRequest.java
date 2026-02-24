package com.aziz.demosec.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboratoryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String address;

    private String phone;
}
