package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.DonationAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationAssignmentRepository extends JpaRepository<DonationAssignment, Long> {
    List<DonationAssignment> findByDonationId(Long donationId);
    List<DonationAssignment> findByAidRequestId(Long aidRequestId);
}
