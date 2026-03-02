package com.aziz.demosec.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "money_donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Amount extends Donation {

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal money;
}
