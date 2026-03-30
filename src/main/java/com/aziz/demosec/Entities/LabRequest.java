package com.aziz.demosec.Entities;

import com.aziz.demosec.dto.RequestedBy;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LabRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CDC : patient peut faire la demande
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // CDC : doctor peut aussi faire la demande — nullable
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "laboratory_id", nullable = false)
    private Laboratory laboratory;

    // Optionnel — juste pour info (PATIENT ou DOCTOR)
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RequestedBy requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LabRequestStatus status;

    // Type d'analyse : "Blood Test", "Urine Test", "MRI"...
    @Column(nullable = false)
    private String testType;

    // Notes cliniques optionnelles
    @Column(columnDefinition = "TEXT")
    private String clinicalNotes;

    // Date souhaitée pour le test
    private LocalDateTime scheduledAt;

    // Date de création — automatique
    @Column(nullable = false)
    private LocalDateTime requestedAt;

    // CDC : notification envoyée quand résultats disponibles
    @Builder.Default
    private boolean notificationSent = false;

    // Relation avec le résultat
    @OneToOne(mappedBy = "labRequest", cascade = CascadeType.ALL)
    private LabResult labResult;
}