package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.HomeCareService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeCareServiceRepository extends JpaRepository<HomeCareService, Long> {
}
