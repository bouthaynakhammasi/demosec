package com.aziz.demosec.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "order_tracking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrderTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private PharmacyOrder order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PharmacyOrderStatus status;

    @Column(length = 1000)
    private String note;

    @Column(nullable = false)
    private String changedBy; // username or role (e.g. "PHARMACIST", "SYSTEM", "PATIENT")

    @Column(nullable = false)
    private LocalDateTime changedAt;
}
