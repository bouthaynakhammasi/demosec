package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.LabRequest;
import com.aziz.demosec.Entities.LabRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabRequestRepository extends JpaRepository<LabRequest, Long> {
    List<LabRequest> findByPatientId(Long patientId);
    List<LabRequest> findByDoctorId(Long doctorId);
    List<LabRequest> findByLaboratoryId(Long laboratoryId);
    List<LabRequest> findByStatus(LabRequestStatus status);
}