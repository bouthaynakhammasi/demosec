package com.aziz.demosec.dto.pharmacy;

import com.aziz.demosec.Entities.ProductType;
import com.aziz.demosec.Entities.ProductUnit;
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
    private ProductType type;
    private ProductUnit unit;
    private String barcode;
}
