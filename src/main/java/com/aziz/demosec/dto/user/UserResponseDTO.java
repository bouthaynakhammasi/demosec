package com.aziz.demosec.dto.user;

import com.aziz.demosec.domain.Role;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String phone;
    private LocalDate birthDate;
    private boolean enabled;
    private String specialty;
    private String photo;
    private String profileImage;
    private Long pharmacyId;
    private String pharmacyName;
}
