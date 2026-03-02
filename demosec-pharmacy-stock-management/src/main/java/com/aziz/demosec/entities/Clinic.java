package com.aziz.demosec.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clinics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String address;

    private Double latitude;
    private Double longitude;
    private String phone;

    @Column(nullable = false)
    private boolean verified;

    @Column(name = "has_emergency", nullable = false)
    private boolean hasEmergency;

    @Column(name = "has_ambulance", nullable = false)
    private boolean hasAmbulance;
    private String emergencyPhone;
    private String ambulancePhone;
}