package com.aziz.demosec.dto.patient;

import com.aziz.demosec.Entities.BloodType;
import com.aziz.demosec.Entities.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PatientRequestDTO {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank 
    @Size(min = 8)
    private String password;

    @Size(min = 8)
    private String phone;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate birthDate;

    @NotNull
    private Gender gender;

    @NotNull
    private BloodType bloodType;

    @NotNull
    private String emergencyContactName;

    @NotNull
    private String emergencyContactPhone;

    private String chronicDiseases;

    private String drugAllergies;

    private String hereditaryDiseases;
}
