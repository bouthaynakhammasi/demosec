package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service_providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ServiceProvider extends User {

    private String certificationDocument;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String profilePictureUrl;

    @Column(nullable = false)
    private double averageRating = 0.0;

    @Column(nullable = false)
    private int totalReviews = 0;

    @ManyToMany
    @JoinTable(
        name = "service_provider_specialties", 
        joinColumns = @JoinColumn(name = "service_provider_id"), 
        inverseJoinColumns = @JoinColumn(name = "home_care_service_id")
    )
    private Set<HomeCareService> specialties = new HashSet<>();
}
