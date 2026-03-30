package com.aziz.demosec.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "weekly_time_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "weekly_day_schedule_id")
    @JsonIgnore
    private WeeklyDaySchedule daySchedule;

    @ManyToOne
    @JoinColumn(name = "exception_id")
    @JsonIgnore
    private ScheduleException scheduleException;

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;

    @Column(nullable = false)
    private String mode;
}
