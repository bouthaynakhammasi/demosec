package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private MedicalEvent event;

    @Column(nullable = false)
    private String zoneName;

    @Column(nullable = false)
    private String seatLabel;

    private int posX;
    private int posY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    private String reservedByFullName;
    
    private Integer rowNumber;
    private Integer seatNumber;
    private Integer tableNumber;
}
