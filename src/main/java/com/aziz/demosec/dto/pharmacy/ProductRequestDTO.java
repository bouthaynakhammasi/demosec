package com.aziz.demosec.dto.pharmacy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String description;
    private String imageUrl;
    private String manufacturer;
    private String brand;
    private String category;

    @NotNull
    private String type;

    @NotNull
    private String unit;

    private String barcode;
}
