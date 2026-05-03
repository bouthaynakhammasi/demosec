package com.aziz.demosec.Entities;

import com.aziz.demosec.Entities.appointment.CalendarAvailability;
import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "provider_calendars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false, unique = true)
    private User provider;

    @Column(nullable = false)
    @Builder.Default
    private String timezone = "UTC";

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CalendarAvailability> availabilities = new ArrayList<>();
}
