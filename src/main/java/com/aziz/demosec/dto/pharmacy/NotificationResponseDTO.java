package com.aziz.demosec.dto.pharmacy;

import com.aziz.demosec.Entities.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {

    private Long id;
    private Long recipientId;
    private Long orderId;
    private Long targetId; // eventId
    private Long participationId;
    private String participationStatus;
    private String eventTitle;
    private String eventDate;
    
    private String title;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String senderName;
}
