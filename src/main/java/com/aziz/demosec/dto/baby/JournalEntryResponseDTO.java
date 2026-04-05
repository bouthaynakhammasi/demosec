package com.aziz.demosec.dto.baby;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class JournalEntryResponseDTO {
    private Long id;
    private Long babyProfileId;
    private String entryType;
    private String value;
    private String notes;
    private String metadata;
    private LocalDateTime createdAt;
}
