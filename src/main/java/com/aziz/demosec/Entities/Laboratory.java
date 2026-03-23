package com.aziz.demosec.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "laboratories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Laboratory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;
    private String phone;
    private boolean active; // ✅ Ajoute ce champ

    @OneToMany(mappedBy = "laboratory", cascade = CascadeType.ALL)
    private List<LaboratoryStaff> staffMembers;
    @OneToMany(mappedBy = "laboratory", cascade = CascadeType.ALL)
    private List<LabTest> labTests;
}