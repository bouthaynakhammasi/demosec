package com.aziz.demosec.entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "nutritionists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Nutritionist extends User {

    @Column(name = "license_number", unique = true)
    private String licenseNumber;
    private String specialties;
    private Integer yearsOfExperience;
    private String bio;

    @Column(nullable = false)
    private boolean verified;
}