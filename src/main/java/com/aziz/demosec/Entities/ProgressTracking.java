package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "goal_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;


    @ManyToOne
    @JoinColumn(name = "goal_id", nullable = false)
    private LifestyleGoal goal;

    private LocalDate date;

    @Column(name = "tracked_value")
    private BigDecimal value;

    private String notes;
}
