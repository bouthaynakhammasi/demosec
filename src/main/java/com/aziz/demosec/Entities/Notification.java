package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    private String title;
    private String message;
    private String type; // "LIKE", "COMMENT"
    @JsonProperty("isRead")
    private boolean isRead;
    private LocalDateTime createdAt;
    
    // Optional: add a link or ID of the related post
    private Long relatedId;
}
