package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {
    List<Vaccination> findByBabyId(Long babyId);
    List<Vaccination> findByBabyIdAndVaccineName(Long babyId, String vaccineName);
    void deleteByBabyId(Long babyId);
}
