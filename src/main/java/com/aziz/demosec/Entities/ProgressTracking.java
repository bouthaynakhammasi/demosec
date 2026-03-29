package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "progress_trackings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tracking_type", nullable = false)
    private TrackingType trackingType;
    @ManyToOne
    @JoinColumn(name = "lifestyle_plan_id")
    private LifestylePlan lifestylePlan;

    @ManyToOne
    @JoinColumn(name = "rehabilitation_program_id")
    private RehabilitationProgram rehabilitationProgram;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @ManyToOne
    @JoinColumn(name = "recorded_by_id")
    private User recordedBy;

    @Column(nullable = false)
    private String metric;

    @Column(name = "result_value", nullable = false)
    private String resultValue;

}