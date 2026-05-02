package com.aziz.demosec.dto.baby;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class JournalSummaryDTO {
    private String type;
    private String content;
    private LocalDateTime time;
}
