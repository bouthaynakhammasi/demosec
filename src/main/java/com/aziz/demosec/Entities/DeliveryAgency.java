package com.aziz.demosec.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "delivery_agencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAgency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String logoUrl;
    private String phoneNumber;
    private String address;
    private String city;

    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("agency")
    private List<DeliveryAgent> agents;
}
