package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "lifestyle_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LifestyleGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lifestyle_plan_id", nullable = false)
    private LifestylePlan lifestylePlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalCategory category;
    private BigDecimal targetValue;
    private BigDecimal baselineValue;
    private LocalDate targetDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status;

}