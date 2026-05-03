package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grocery_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroceryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lifestyle_plan_id", nullable = false)
    private LifestylePlan lifestylePlan;

    @Column(nullable = false)
    private String itemName;

    private String quantity;
    private String unit;

    private boolean purchased = false;

    private String sourceRecipe;

    @org.hibernate.annotations.CreationTimestamp
    @Column(updatable = false)
    private java.time.LocalDateTime createdAt;
}
