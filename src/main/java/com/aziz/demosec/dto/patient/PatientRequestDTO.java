package com.aziz.demosec.dto.patient;

import com.aziz.demosec.Entities.BloodType;
import com.aziz.demosec.Entities.Gender;
import com.aziz.demosec.domain.Role;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PatientRequestDTO {

    @NotBlank
    String fullName;

    @Email @NotBlank
    String email;

    @NotBlank @Size(min = 6)
    String password;

    @Size(min = 8)
    String phone;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    LocalDate birthDate;

    @NotNull
    Gender gender;
    @NotNull
    BloodType bloodType;
    @NotNull
    String emergencyContactName;
    @NotNull
    String emergencyContactPhone;

    String chronicDiseases;

    String drugAllergies;

    String hereditaryDiseases;
}