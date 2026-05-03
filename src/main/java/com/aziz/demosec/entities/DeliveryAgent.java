package com.aziz.demosec.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_agents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAgent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phoneNumber;
    private String vehicleType; // e.g., MOTORCYCLE, CAR, BICYCLE
    private String vehiclePlate;
    
    @Enumerated(EnumType.STRING)
    private AgentStatus status; // AVAILABLE, BUSY, OFFLINE

    @ManyToOne
    @JoinColumn(name = "agency_id")
    @JsonIgnoreProperties("agents")
    private DeliveryAgency agency;

    public enum AgentStatus {
        AVAILABLE, BUSY, OFFLINE
    }
}
