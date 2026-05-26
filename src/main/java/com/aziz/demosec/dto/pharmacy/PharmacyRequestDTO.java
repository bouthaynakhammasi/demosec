package com.aziz.demosec.dto.pharmacy;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyRequestDTO {

    @NotBlank(message = "Pharmacy name is required")
    private String name;
    @NotBlank(message = "Address is required")
    private String address;
    private Double locationLat;
    private Double locationLng;
    @Size(min = 8, message = "Phone number must be at least 8 characters long")
    private String phoneNumber;
    @Email(message = "Invalid email format")
    private String email;
}
