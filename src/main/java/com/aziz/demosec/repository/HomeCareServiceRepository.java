package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.HomeCareService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeCareServiceRepository extends JpaRepository<HomeCareService, Long> {
    Optional<HomeCareService> findByName(String name);
    List<HomeCareService> findByActiveTrue();
    List<HomeCareService> findByCategoryAndActiveTrue(String category);
}
