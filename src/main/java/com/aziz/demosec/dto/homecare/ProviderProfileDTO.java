package com.aziz.demosec.dto.homecare;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderProfileDTO {
    private Long id;
    private String fullName;
    private String bio;
    private String profilePictureUrl;
    private double averageRating;
    private int totalReviews;
    private String certificationDocument;
    private List<ServiceSummaryDTO> specialties;
    private List<ReviewDTO> reviews;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceSummaryDTO {
        private Long id;
        private String name;
        private String category;
        private String iconUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewDTO {
        private int rating;
        private String comment;
        private String createdAt;
        private String patientName;
    }
}
