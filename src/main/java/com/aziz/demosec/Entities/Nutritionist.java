package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@DiscriminatorValue("NUTRITIONIST")
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
