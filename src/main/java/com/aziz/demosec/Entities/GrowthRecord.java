package com.aziz.demosec.Entities;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "growth_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrowthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "baby_id", nullable = false)
    private BabyProfile baby;

    // weight in kg
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    // height in cm
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal height;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

}