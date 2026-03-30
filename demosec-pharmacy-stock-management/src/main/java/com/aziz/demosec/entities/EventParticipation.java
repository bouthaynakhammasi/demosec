package com.aziz.demosec.entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_participations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private MedicalEvent event;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    private LocalDateTime registeredAt = LocalDateTime.now();

    private String status = "PENDING"; // PENDING, CONFIRMED, REJECTED
}
