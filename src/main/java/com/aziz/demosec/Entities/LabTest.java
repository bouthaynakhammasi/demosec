package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lab_tests")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "laboratory_id", nullable = false)
    private Laboratory laboratory;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "test_type", nullable = false)
    private TestType testType;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "gender_specific")
    private String genderSpecific;           // ALL, MALE, FEMALE

    @Builder.Default
    @Column(name = "requires_appointment", nullable = false)
    private Boolean requiresAppointment = false;

    @Builder.Default
    @Column(name = "requires_fasting", nullable = false)
    private Boolean requiresFasting = false;
}