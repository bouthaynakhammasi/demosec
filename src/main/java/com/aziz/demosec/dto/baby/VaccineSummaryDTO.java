package com.aziz.demosec.dto.baby;

import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class VaccineSummaryDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate dueDate;
    private String status; // DONE, UPCOMING, OVERDUE
}
