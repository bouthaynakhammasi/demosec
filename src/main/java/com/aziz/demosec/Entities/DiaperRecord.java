package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "diaper_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DiaperRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baby_profile_id", nullable = false)
    private BabyProfile babyProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaperType diaperType;

    private boolean rashNoted;
    private String stoolColor;
    private String stoolTexture;
    
    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime changedAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (changedAt == null) changedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
