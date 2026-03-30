package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Patient extends User {

    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Enumerated(EnumType.STRING)
    private BloodType bloodType;
    
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Health metrics / Info
    private String glucoseRate;
    private String allergies;
    private String diseases;
    
    private Double height;
    private Double weight;

    // Detailed fields used in PatientServiceImpl
    private String chronicDiseases;
    private String drugAllergies;
    private String hereditaryDiseases;
}
