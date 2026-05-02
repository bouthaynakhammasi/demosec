package com.aziz.demosec.dto.donation;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationAssignmentResponseDTO {
    private Long id;
    private Long donationId;
    private Long aidRequestId;
    private String patientName;
    private LocalDateTime assignedAt;
}
