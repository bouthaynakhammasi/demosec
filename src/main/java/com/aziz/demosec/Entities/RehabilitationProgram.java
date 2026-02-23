package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rehabilitation_programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RehabilitationProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private User provider;

    @Column(nullable = false)
    private String name;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}