package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service_providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProvider extends User {

    private String certificationDocument;

    @ManyToMany
    @JoinTable(
            name = "service_provider_specialties",
            joinColumns = @JoinColumn(name = "service_provider_id"),
            inverseJoinColumns = @JoinColumn(name = "home_care_service_id")
    )
    private Set<HomeCareService> specialties = new HashSet<>();

}
