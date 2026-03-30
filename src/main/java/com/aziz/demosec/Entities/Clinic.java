package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "clinic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Clinic extends User {

    @Column(name = "name", nullable = false)
    private String name;

    private String address;

    private String phone;

    private Double latitude;
    private Double longitude;

    @Column(name = "has_emergency", nullable = false, columnDefinition = "boolean default false")
    private boolean hasEmergency;

    @Column(name = "has_ambulance", nullable = false, columnDefinition = "boolean default false")
    private boolean hasAmbulance;

    private String emergencyPhone;
    private String ambulancePhone;

    // FIX: removed inline "= false", kept columnDefinition for DB default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean verified;
}