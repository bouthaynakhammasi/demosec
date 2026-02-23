package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pharmacy_stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer minQuantityThreshold;

}