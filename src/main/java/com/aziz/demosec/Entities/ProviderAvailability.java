package com.aziz.demosec.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "provider_availability")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProviderAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ServiceProvider provider;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;       // règle hebdomadaire répétée

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Builder.Default
    @Column(nullable = false)
    private boolean available = true;

    private LocalDate specificDate;    // null = règle répétée, non-null = exception ponctuelle
}
