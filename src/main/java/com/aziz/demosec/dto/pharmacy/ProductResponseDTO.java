package com.aziz.demosec.dto.pharmacy;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String manufacturer;
    private String brand;
    private String category;
    private String type;
    private String unit;
    private String barcode;
}
