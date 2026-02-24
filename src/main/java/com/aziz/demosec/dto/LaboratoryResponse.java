package com.aziz.demosec.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboratoryResponse {

    private Long id;
    private String name;
    private String address;
    private String phone;
}
