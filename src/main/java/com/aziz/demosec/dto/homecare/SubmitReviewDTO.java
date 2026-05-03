package com.aziz.demosec.dto.homecare;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubmitReviewDTO {
    private int rating;      // 1 à 5
    @Size(max = 1000, message = "Le commentaire ne peut pas dépasser 1000 caractères")
    private String comment;
}
