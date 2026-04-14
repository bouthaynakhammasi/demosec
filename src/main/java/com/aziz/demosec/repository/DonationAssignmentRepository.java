package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Donation;
import com.aziz.demosec.Entities.DonationAssignment;
import com.aziz.demosec.Entities.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DonationAssignmentRepository extends JpaRepository<DonationAssignment, Long> {

    List<DonationAssignment> findByDonationId(Long donationId);
    List<DonationAssignment> findByAidRequestId(Long aidRequestId);

    /**
     * Keyword-based multi-table query — spans DonationAssignment → AidRequest → Patient
     * to find all donations linked to a specific patient through their aid requests,
     * filtered by donation status.
     */
    @Query("""
            SELECT da.donation FROM DonationAssignment da
            WHERE da.aidRequest.patient.id = :patientId
              AND da.donation.status = :status
            """)
    List<Donation> findDonationsByPatientIdAndStatus(
            @Param("patientId") Long patientId,
            @Param("status") DonationStatus status);
}
