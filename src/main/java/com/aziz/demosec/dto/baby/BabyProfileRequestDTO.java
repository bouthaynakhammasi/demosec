package com.aziz.demosec.dto.baby;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BabyProfileRequestDTO {
    private String name;
    private LocalDate birthDate;
    private String gender;
    private Double birthWeight;
    private Double birthHeight;
    private List<String> priorities; // e.g. ["feeding", "sleep"]
}
