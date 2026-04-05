package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("DOCTOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Doctor extends User {

    private String specialty;
    @Column(name = "license_number", nullable = false)
    private String licenseNumber;
    private Integer yearsOfExperience;
    private BigDecimal consultationFee;
    @Enumerated(EnumType.STRING)
    private ConsultationMode consultationMode;
}