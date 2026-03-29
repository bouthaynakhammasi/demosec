package com.aziz.demosec.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PharmacyResponse {
    private Long id;
    private String name;
    private String address;
    private Double locationLat;
    private Double locationLng;
    private String phoneNumber;
    private String email;
}