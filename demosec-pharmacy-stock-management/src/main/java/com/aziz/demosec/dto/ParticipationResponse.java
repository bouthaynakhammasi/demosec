package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationResponse {
    private Long id;
    private Long eventId;
    private Long userId;
    private String userFullName;
    private String userEmail;
    private String status;
    private LocalDateTime registeredAt;
    private boolean attended;
}
