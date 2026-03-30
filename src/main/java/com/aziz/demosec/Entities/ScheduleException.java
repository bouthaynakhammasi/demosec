package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedule_exceptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long providerId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate endDate;

    private String reason;

    @JsonProperty("isAvailable")
    @Column(nullable = false)
    private boolean isAvailable; // false = Day Off, true = Special Hours

    @Enumerated(EnumType.STRING)
    private ExceptionType type;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "exception_id")
    @Builder.Default
    private List<WeeklyTimeSlot> timeSlots = new ArrayList<>();

    @com.fasterxml.jackson.annotation.JsonSetter("isAvailable")
    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
