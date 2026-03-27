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

    String phone;
    LocalDate birthDate;

    Gender gender;
    BloodType bloodType;
    String emergencyContactName;
    String emergencyContactPhone;
    String chronicDiseases;
    String drugAllergies;
    String hereditaryDiseases;
}
