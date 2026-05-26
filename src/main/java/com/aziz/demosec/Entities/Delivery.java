package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private PharmacyOrder order;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private DeliveryAgent agent;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private DeliveryAgency agency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    private String trackingNumber;
    private String agencyName;
    private String externalTrackingId;
    private String trackingUrl;

    private String courierName;
    private String courierPhone;

    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime estimatedArrival; // For compatibility with legacy code

    private LocalDateTime requestedAt;
    private LocalDateTime deliveredAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = DeliveryStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
