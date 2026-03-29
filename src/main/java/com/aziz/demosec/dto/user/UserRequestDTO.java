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

    @NotBlank @Size(min = 8)
    String password;

    @NotNull
    Role role;

    @NotNull @Size(min = 8)
    String phone;
    
    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    LocalDate birthDate;
}

