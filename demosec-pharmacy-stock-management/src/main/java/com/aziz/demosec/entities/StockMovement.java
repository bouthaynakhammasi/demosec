package com.aziz.demosec.entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pharmacy_stock_id", nullable = false)
    private PharmacyStock pharmacyStock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockMovementType movementType;

    @Column(nullable = false)
    private Integer quantity;
    private String reference;

    @ManyToOne
    @JoinColumn(name = "performed_by_id")
    private User performedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}