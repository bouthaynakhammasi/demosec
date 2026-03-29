package com.aziz.demosec.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PharmacyRequest {

    @NotBlank(message = "Pharmacy name is required")
    @Size(max = 100, message = "Pharmacy name must not exceed 100 characters")
    private String name;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double locationLat;

    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double locationLng;

    @Pattern(
            regexp = "^[0-9+()\\-\\s]{8,20}$",
            message = "Phone number format is invalid"
    )
    private String phoneNumber;

    @Email(message = "Email format is invalid")
    @Size(max = 120, message = "Email must not exceed 120 characters")
    private String email;
}