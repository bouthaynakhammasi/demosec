package com.aziz.demosec.dto.pharmacy;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyResponseDTO {

    private Long id;
    private String name;
    private String address;
    private Double locationLat;
    private Double locationLng;
    private String phoneNumber;
    private String email;
}
