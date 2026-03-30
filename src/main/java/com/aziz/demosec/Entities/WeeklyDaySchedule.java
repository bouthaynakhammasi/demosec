package com.aziz.demosec.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "weekly_day_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyDaySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "weekly_schedule_id", nullable = false)
    @JsonIgnore
    private WeeklySchedule weeklySchedule;

    @Column(nullable = false)
    private String dayOfWeek;

    @Column(nullable = false, name = "enabled")
    private boolean enabled;

    @Column(nullable = false, name = "active")
    private boolean active;

    // Helper to keep both fields in sync
    public void setActive(boolean active) {
        this.active = active;
        this.enabled = active;
    }

    @OneToMany(mappedBy = "daySchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeeklyTimeSlot> timeSlots = new ArrayList<>();
}
