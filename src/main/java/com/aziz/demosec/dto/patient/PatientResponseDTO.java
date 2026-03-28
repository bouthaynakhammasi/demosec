package com.aziz.demosec.dto.patient;

import com.aziz.demosec.Entities.BloodType;
import com.aziz.demosec.Entities.Gender;
import com.aziz.demosec.domain.Role;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PatientResponseDTO {
    Long id;
    String fullName;
    String email;
    String phone;
    LocalDate birthDate;
    boolean enabled;
    Gender gender;
    BloodType bloodType;
    String emergencyContactName;
    String emergencyContactPhone;
    String chronicDiseases;
    String drugAllergies;
    String hereditaryDiseases;
}