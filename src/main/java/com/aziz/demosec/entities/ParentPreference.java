package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parent_preferences")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParentPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "baby_profile_id", nullable = false)
    private BabyProfile babyProfile;

    @Column(nullable = false)
    private String priorityType; // feeding, sleep, vaccines, development, health, self-care

    private boolean selected;
}
