package com.aziz.demosec.dto.baby;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BabyProfileResponseDTO {
    private Long id;
    private Long patientId;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private Double birthWeight;
    private Double birthHeight;
    private String photoUrl;
    private List<ParentPreferenceDTO> preferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
