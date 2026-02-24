package com.aziz.demosec.dto.donation;

import com.aziz.demosec.Entities.DonationStatus;
import com.aziz.demosec.Entities.DonationType;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationResponseDTO {
    private Long id;
    private String donorName;
    private DonationType type;
    private DonationStatus status;
    private Double amount;
    private String categorie;
    private String description;
    private Integer quantite;
    private LocalDateTime createdAt;
}
