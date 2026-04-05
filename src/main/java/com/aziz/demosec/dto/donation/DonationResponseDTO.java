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
    private Long creatorId;
    private String donorName;
    private String donorProfileImage;
    private DonationType type;
    private DonationStatus status;
    private Double amount;
    private String categorie;
    private String description;
    private Integer quantite;
    private String imageData;
    private LocalDateTime createdAt;
}
