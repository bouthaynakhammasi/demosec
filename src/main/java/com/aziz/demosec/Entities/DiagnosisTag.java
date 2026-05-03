package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diagnosis_tags")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DiagnosisTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Diabetes", "Hypertension", "Anemia"
}
