package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

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
    private BigDecimal consultationFee;
    @Enumerated(EnumType.STRING)
    private ConsultationMode consultationMode;

}