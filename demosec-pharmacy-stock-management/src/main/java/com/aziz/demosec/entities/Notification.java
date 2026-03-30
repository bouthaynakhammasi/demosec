package com.aziz.demosec.entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String message;        // "John Doe joined Event: Annual Summit"
    String type;           // "EVENT_JOIN"
    Long targetId;         // event ID
    @Column(name = "participation_id")
    Long participationId; // context for join requests
    
    @Builder.Default
    @Column(name = "is_read")
    boolean read = false;
    
    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "recipient_id")  // the ADMIN user
    User recipient;

    @ManyToOne
    @JoinColumn(name = "sender_id")     // the PATIENT user
    User sender;
}
