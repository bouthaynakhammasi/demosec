package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clinics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Clinic extends User {

    @Column(name = "name", nullable = false)
    private String name;

    private String address;

    private Double latitude;
    private Double longitude;

    @Column(name = "has_emergency", nullable = false)
    private boolean hasEmergency;

    @Column(name = "has_ambulance", nullable = false)
    private boolean hasAmbulance;
    private String emergencyPhone;
    private String ambulancePhone;

    // Aliases to avoid breaking API references using getName or getPhone implicitly
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setFullName(name);
    }
}