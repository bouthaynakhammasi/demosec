package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PrescriptionItem> items = new ArrayList<>();

}