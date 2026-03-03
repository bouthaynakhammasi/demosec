package com.aziz.demosec.dto;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LaboratoryResponse {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String openingHours;
    private String specializations;
    private boolean active;
    private int totalStaff;
    private int totalTests;
}
