package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Nutritionist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
@Repository
public interface NutritionistRepository extends JpaRepository<Nutritionist, Long> {
=======
import java.util.Optional;

@Repository
public interface NutritionistRepository extends JpaRepository<Nutritionist, Long> {
    Optional<Nutritionist> findByEmail(String email);
>>>>>>> origin/MedicalRecord
}
