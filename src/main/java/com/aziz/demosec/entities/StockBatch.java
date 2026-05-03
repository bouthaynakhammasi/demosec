package com.aziz.demosec.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pharmacy_stock_id", nullable = false)
    private PharmacyStock pharmacyStock;
    private String batchNumber;

    @Column(nullable = false)
    private Integer quantity;
    private LocalDate expirationDate;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;

    @Column(nullable = false)
    private LocalDateTime receivedAt;

}