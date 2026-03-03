package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lab_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_request_id")
    private LabRequest labRequest;

    @Column(nullable = false)
    private String resultFile;

    @Lob
    @Column(nullable = false)
    private String resultData;

    @Column(length = 100)
    private String testType;

    private String normalValue;

    @Column(length = 500)
    private String abnormalFindings;

    @Column(length = 100)
    private String technicianName;

    @Column(length = 100)
    private String verifiedBy;

    @Builder.Default
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isAbnormal = false;

    @Column(length = 1000)
    private String recommendations;

    @Builder.Default
    @Column(columnDefinition = "INT DEFAULT 3")
    private Integer priority = 3;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Builder.Default
    @Column(length = 20)
    private String status = "PENDING";

    @Builder.Default
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isUrgent = false;

    @Column(length = 50)
    private String resultCategory;

    @PrePersist
    protected void onCreate() {
        if (completedAt == null) {
            completedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
        if (priority == null) {
            priority = 3;
        }
        if (isAbnormal == null) {
            isAbnormal = false;
        }
        if (isUrgent == null) {
            isUrgent = false;
        }
    }
}