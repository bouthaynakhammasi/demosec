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

    // ✅ AJOUTÉ : champ manquant référencé par mappedBy = "calendar"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private ProviderCalendar calendar;

    @Column(name = "appointment_duration")
    private int appointmentDuration = 30;

    @Column(name = "doctor_id")
    private Long doctorId;

    @OneToMany(mappedBy = "weeklySchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeeklyDaySchedule> days = new ArrayList<>();
}