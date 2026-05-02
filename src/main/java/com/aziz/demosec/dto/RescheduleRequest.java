package com.aziz.demosec.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RescheduleRequest {
    private Long newProviderId;
    private String newDate;
    private String newStartTime;
    private String newEndTime;
    private String newMode;
}
