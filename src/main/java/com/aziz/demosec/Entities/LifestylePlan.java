package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lifestyle_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LifestylePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "nutritionist_id")
    private User nutritionist;

    @Column(nullable = false)
    private String title;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "lifestylePlan", cascade = CascadeType.ALL)
    private List<LifestyleGoal> goals = new ArrayList<>();

}