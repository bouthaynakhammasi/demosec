package com.aziz.demosec.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboratoryStaffResponse {
    private Long id;
    private String fullName;
    private String email;
    private Long laboratoryId;
    private String role;        // ✅ Ajouté
    private boolean active;     // ✅ Ajouté
    private LocalDateTime createdAt;
}