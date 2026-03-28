package com.aziz.demosec.dto.baby;

import com.aziz.demosec.Entities.JournalEntryType;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class JournalEntryRequestDTO {
    private JournalEntryType type;
    private String value;
    private String notes;
}
