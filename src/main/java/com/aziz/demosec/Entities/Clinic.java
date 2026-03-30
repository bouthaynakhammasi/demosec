package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "clinic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String address;
    private String phone;



    @Column(name = "has_emergency", nullable = false)
    private boolean hasEmergency;

    @Column(name = "has_ambulance", nullable = false)
    private boolean hasAmbulance;
    private String emergencyPhone;
    private String ambulancePhone;
    private String Latitude ;
    private String Longitude;


    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean verified = false;
}

