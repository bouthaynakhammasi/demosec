package com.aziz.demosec.dto.request;

import com.aziz.demosec.entities.ProductType;
import com.aziz.demosec.entities.ProductUnit;
import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private String imageUrl;
    private String manufacturer;
    private String brand;
    private String category;
    private ProductType type;
    private String barcode;
    private ProductUnit unit;
}