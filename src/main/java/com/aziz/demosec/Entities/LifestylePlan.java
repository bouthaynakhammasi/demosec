package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lifestyle_plan")
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
    @JoinColumn(name = "goal_id", nullable = false)
    private LifestyleGoal goal;

    
    @ManyToOne
    @JoinColumn(name = "nutritionist_id", nullable = false)
    private User nutritionist;

    @Column(nullable = false)
    private String title;

    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}