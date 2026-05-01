package com.aziz.demosec.entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "seat_label"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private MedicalEvent event;

    @Column(name = "zone_name")
    private String zoneName; // e.g. "Zone A", "VIP", "Balcony"

    @Column(name = "seat_label", nullable = false)
    private String seatLabel; // e.g. "A-12"

    @Column(name = "pos_x")
    private Double posX;

    @Column(name = "pos_y")
    private Double posY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_by_id")
    private User reservedBy;
}
