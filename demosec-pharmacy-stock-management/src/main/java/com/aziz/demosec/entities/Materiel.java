package com.aziz.demosec.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "materiel_donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Materiel extends Donation {

    @Column(nullable = false)
    private Integer quantite;
}
