package com.aziz.demosec.dto.donation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationAssignmentDTO {
    private Long donationId;
    private Long aidRequestId;
}
