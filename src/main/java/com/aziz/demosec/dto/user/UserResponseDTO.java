package com.aziz.demosec.dto.user;

import com.aziz.demosec.domain.Role;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponseDTO {
    Long id;
    String fullName;
    String email;
    Role role;
    String phone;
    LocalDate birthDate;
    boolean enabled;
    String specialty;
}
