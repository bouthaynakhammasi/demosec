package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "healthy_recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthyRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String ingredients;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String instructions;

    @Embedded
    private Nutrients nutrients;

    private Integer prepTimeMinutes;

    private String category; // e.g. "Breakfast", "Lunch", "Dinner", "Snack"

    @ManyToMany
    @JoinTable(
        name = "recipe_diagnosis_tags",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<DiagnosisTag> allowedDiagnoses = new ArrayList<>();

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutritionist_id", nullable = false)
    private Nutritionist nutritionist;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
