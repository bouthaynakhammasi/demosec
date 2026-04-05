package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
<<<<<<< HEAD
    Optional<ServiceProvider> findByUser_Id(Long userId);
=======
    Optional<ServiceProvider> findByEmail(String email);
>>>>>>> origin/MedicalRecord
}
