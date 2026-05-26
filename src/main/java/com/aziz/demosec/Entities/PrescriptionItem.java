package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prescription_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    private String medicationName;

    @Builder.Default
    @Column(nullable = false)
    private Integer quantity = 1;

    private String dosage;
    private String frequency;
    private String duration;
}
