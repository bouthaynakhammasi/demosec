package com.aziz.demosec.dto.baby;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ParentPreferenceDTO {
    private Long id;
    private String priorityType;
    private boolean selected;
}
