package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
@Entity
@Table(name = "donations")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;




    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationType type;

    private String donorName;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationStatus status = DonationStatus.AVAILABLE;

    // ✅ Champs MONEY (nullable si type=MATERIEL)
    private Double amount;

    // ✅ Champs MATERIELe (nullable si type=MONEY)
    private String categorie;
    private String description;
    private Integer quantite;

    @Column(name="creator_id")
    private Long creatorId;

    @Lob
    @Column(name="photo_data", columnDefinition="LONGTEXT")
    private String photoData;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}