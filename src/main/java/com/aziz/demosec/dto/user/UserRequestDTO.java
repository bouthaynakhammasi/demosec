package com.aziz.demosec.dto.user;

import com.aziz.demosec.domain.Role;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserRequestDTO {
    @NotBlank
    String fullName;

    @Email @NotBlank
    String email;

    @Size(min = 6)
    String password;

    @NotNull
    Role role;

    String phone;
    LocalDate birthDate;
}
