package com.aziz.demosec.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_availabilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "calendar_id", nullable = false)
    private ProviderCalendar calendar;

    @Column( nullable = false)
    private LocalDateTime startTime;

    @Column( nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityMode mode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus status;
    private String address;

}