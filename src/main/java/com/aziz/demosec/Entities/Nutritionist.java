package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

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
    private BigDecimal consultationFee;
    @Enumerated(EnumType.STRING)
    private ConsultationMode consultationMode;

<<<<<<< HEAD
    @Column(nullable = false)
    private boolean verified;
}
=======
}
>>>>>>> origin/MedicalRecord
