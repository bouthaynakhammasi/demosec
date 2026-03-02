package com.aziz.demosec.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "vaccinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "baby_id", nullable = false)
    private BabyProfile baby;

    @Column(nullable = false)
    private String vaccineName;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VaccinationStatus status;

}