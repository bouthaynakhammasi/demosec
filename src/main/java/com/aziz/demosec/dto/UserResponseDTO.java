package com.aziz.demosec.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private boolean enabled;
    private String phone;
    private LocalDate birthDate;
}
