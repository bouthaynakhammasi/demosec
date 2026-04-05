package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByDoctorId(Long doctorId);
    
    // Pour calculer facilement la moyenne et le nombre de patients
    long countByDoctorId(Long doctorId);
}
