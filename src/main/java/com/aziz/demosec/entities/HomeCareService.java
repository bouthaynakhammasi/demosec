package com.aziz.demosec.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "home_care_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeCareService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
}