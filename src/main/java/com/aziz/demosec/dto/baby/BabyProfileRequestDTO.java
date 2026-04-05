package com.aziz.demosec.dto.baby;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BabyProfileRequestDTO {
    @NotBlank(message = "Le nom du bébé est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit comporter entre 2 et 50 caractères")
    private String name;

    @NotNull(message = "La date de naissance est obligatoire")
    @PastOrPresent(message = "La date de naissance ne peut pas être dans le futur")
    private LocalDate birthDate;

    @NotBlank(message = "Le genre est obligatoire")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "Le genre doit être 'Garçon' (MALE) ou 'Fille' (FEMALE)")
    private String gender;

    @NotNull(message = "Le poids à la naissance est obligatoire")
    @Min(value = 0, message = "Le poids ne peut pas être négatif")
    @Max(value = 15, message = "Le poids semble invalide (max 15kg à la naissance)")
    private Double birthWeight;

    @NotNull(message = "La taille à la naissance est obligatoire")
    @Min(value = 0, message = "La taille ne peut pas être négative")
    @Max(value = 80, message = "La taille semble invalide (max 80cm à la naissance)")
    private Double birthHeight;

    @NotBlank(message = "La photo du bébé est obligatoire")
    private String photoUrl;

    @NotEmpty(message = "Veuillez choisir au moins une priorité")
    private List<String> priorities; // e.g. ["feeding", "sleep"]

    @AssertTrue(message = "L'application est réservée aux bébés de moins de 1 an")
    public boolean isAgeValid() {
        if (birthDate == null) return true;
        return birthDate.isAfter(LocalDate.now().minusYears(1));
    }
}
