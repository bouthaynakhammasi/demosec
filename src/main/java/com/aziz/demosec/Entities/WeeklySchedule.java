package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "weekly_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "provider_id", nullable = false, unique = true)
    private User provider;

    @Column(name = "appointment_duration")
    @Builder.Default
    private int appointmentDuration = 30;

    @Column(name = "doctor_id")
    private Long doctorId; // Found in DB screenshot, adding to avoid missing column errors

    @OneToMany(mappedBy = "weeklySchedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<WeeklyDaySchedule> days = new ArrayList<>();
}
