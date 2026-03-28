package com.aziz.demosec.dto.baby;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SleepDayDTO {
    private String day; // ex: "Mon"
    private Double hours;
    private long totalSeconds;
}
