package com.aziz.demosec.dto;
 
import jakarta.validation.constraints.*;
import lombok.Data;
 
@Data
public class ReviewRequest {
    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note doit être au moins de 1 étoile")
    @Max(value = 5, message = "La note ne peut pas dépasser 5 étoiles")
    private Integer rating;
 
    @NotBlank(message = "Le commentaire ne peut pas être vide")
    @Size(min = 10, max = 500, message = "Le commentaire doit faire entre 10 et 500 caractères")
    private String comment;
 
    private Boolean isAnonymous = false;
}
