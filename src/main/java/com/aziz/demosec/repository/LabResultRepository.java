package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    Optional<LabResult> findByLabRequestId(Long labRequestId);
    boolean existsByLabRequestId(Long labRequestId);
    List<LabResult> findByLabRequest_Laboratory_Id(Long laboratoryId);
}
