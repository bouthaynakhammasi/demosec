package com.aziz.demosec.service;

import com.aziz.demosec.dto.request.ProductRequest;
import com.aziz.demosec.dto.response.ProductResponse;

import java.util.List;

public interface IProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    ProductResponse getById(Long id);
    List<ProductResponse> getAll();
    void delete(Long id);
}