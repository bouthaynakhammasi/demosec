package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_entries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baby_profile_id", nullable = false)
    private BabyProfile babyProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JournalEntryType entryType; // FEEDING, SLEEP, DIAPER, TEMPERATURE, NOTE

    private String value; // Weight, Temp, Feed amount, etc.
    
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
