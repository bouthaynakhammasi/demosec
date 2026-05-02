package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "daily_health_report")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyHealthReport {

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
    private LocalDate reportDate;

    // Calories
    private Integer actualCalories;
    private Integer expectedCalories;
    private Integer calorieDifference;   // actual - expected

    // Weight
    private Double currentWeight;
    private Double goalWeight;
    private Double weightDifference;     // current - goal

    // Flags
    private boolean missedLog;
    private boolean anomalyDetected;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "report_anomalies",
            joinColumns = @JoinColumn(name = "report_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "anomaly_type")
    private List<AnomalyType> anomalies;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime generatedAt;
}