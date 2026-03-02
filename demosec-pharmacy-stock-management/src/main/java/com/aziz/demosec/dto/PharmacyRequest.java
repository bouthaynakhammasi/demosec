package com.aziz.demosec.dto.request;

import lombok.Data;

@Data
public class PharmacyRequest {
    private String name;
    private String address;
    private Double locationLat;
    private Double locationLng;
    private String phoneNumber;
    private String email;
}