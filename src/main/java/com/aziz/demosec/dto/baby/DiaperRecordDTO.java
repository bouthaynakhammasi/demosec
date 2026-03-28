package com.aziz.demosec.dto.baby;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DiaperRecordDTO {
    private Long id;
    private Long babyId;
    private String diaperType; // WET, DIRTY, MIXED
    private boolean rashNoted;
    private String stoolColor;
    private String stoolTexture;
    private String notes;
    private LocalDateTime changedAt;
}
