package com.aziz.demosec.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String role;
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    // Pharmacy fields
    private String pharmacyName;
    private String pharmacyAddress;
    private String pharmacyPhone;
    private String pharmacyEmail;
    private String diplomaDocument;
}
