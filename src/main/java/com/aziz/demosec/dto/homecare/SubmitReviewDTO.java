package com.aziz.demosec.dto.homecare;

import lombok.Data;

@Data
public class SubmitReviewDTO {
    private int rating;      // 1 à 5
    private String comment;
}
