package com.aziz.demosec.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private SmartDevice device;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmergencySeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmergencyAlertStatus status;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}
