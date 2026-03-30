package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String certificationDocument;

    @Column(nullable = false)
    private boolean verified;

    private String bio;
    private String profilePictureUrl;

    @Builder.Default
    @Column(nullable = false)
    private double averageRating = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private int totalReviews = 0;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "service_provider_specialties",
            joinColumns = @JoinColumn(name = "service_provider_id"),
            inverseJoinColumns = @JoinColumn(name = "home_care_service_id")
    )
    private Set<HomeCareService> specialties = new HashSet<>();


}
