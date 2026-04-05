package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Donation;
import com.aziz.demosec.Entities.DonationStatus;
import com.aziz.demosec.Entities.DonationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByStatus(DonationStatus status);
    List<Donation> findByType(DonationType type);
}
