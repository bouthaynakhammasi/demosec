package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    // Patient owns the goal
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalCategory category;

    private BigDecimal targetValue;
    private BigDecimal baselineValue;

    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status;

   
    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LifestylePlan> plans = new ArrayList<>();


    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgressTracking> progressRecords = new ArrayList<>();
}