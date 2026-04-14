package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.AidRequest;
import com.aziz.demosec.Entities.AidRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AidRequestRepository extends JpaRepository<AidRequest, Long> {

    List<AidRequest> findByPatientId(Long patientId);
    List<AidRequest> findByStatus(AidRequestStatus status);

    /**
     * Used by the nightly scheduler: bulk-reject PENDING aid requests created
     * before the given cutoff in a single UPDATE statement.
     */
    @Modifying
    @Query("""
            UPDATE AidRequest r
            SET r.status = com.aziz.demosec.Entities.AidRequestStatus.REJECTED
            WHERE r.status = com.aziz.demosec.Entities.AidRequestStatus.PENDING
              AND r.createdAt < :cutoff
            """)
    int rejectOldAidRequests(@Param("cutoff") LocalDateTime cutoff);
}
