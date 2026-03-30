package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_results")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_request_id")
    private LabRequest labRequest;

    @Column(nullable = false)
    private String resultData;

    @Column(length = 100)
    private String technicianName;

    private String verifiedBy;

    @Column(length = 500)
    private String abnormalFindings;

    @Builder.Default
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isAbnormal = false;

    @Builder.Default
    @Column(length = 20)
    private String status = "PENDING";

    @Column(name = "completed_at")
    private LocalDateTime completedAt;



    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @PrePersist
    protected void onCreate() {
        if (completedAt == null) completedAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
        if (isAbnormal == null) isAbnormal = false;
    }
}