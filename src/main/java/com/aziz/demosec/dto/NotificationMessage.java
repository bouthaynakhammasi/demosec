package com.aziz.demosec.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessage {

    private String id;
    private String title;
    private String message;
    private String type;        // "aid_request" | "info" | "warning"
    private Long targetUserId;  // null = admin, non-null = patient spécifique
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public static NotificationMessage of(String title, String message, String type, Long targetUserId) {
        return NotificationMessage.builder()
                .id(String.valueOf(System.currentTimeMillis()))
                .title(title)
                .message(message)
                .type(type)
                .targetUserId(targetUserId)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
