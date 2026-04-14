package com.aziz.demosec.dto.donation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopDonorDTO {
    private Long donorId;
    private String donorName;
    private String donorProfileImage;
    private Long assignmentCount;
}
