package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Product;
import com.aziz.demosec.Entities.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByType(ProductType type);
}
