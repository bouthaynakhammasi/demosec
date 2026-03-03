package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.LabRequest;
import com.aziz.demosec.Entities.LabRequestStatus;
import com.aziz.demosec.dto.RequestedBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LabRequestRepository extends JpaRepository<LabRequest, Long> {

    List<LabRequest> findByPatientId(Long patientId);
    List<LabRequest> findByDoctorId(Long doctorId);
    List<LabRequest> findByLaboratoryId(Long laboratoryId);
    List<LabRequest> findByStatus(LabRequestStatus status);
    List<LabRequest> findByRequestedBy(RequestedBy requestedBy);
    List<LabRequest> findByPatientIdOrderByRequestedAtDesc(Long patientId);
    List<LabRequest> findByStatusAndNotificationSentFalse(LabRequestStatus status);
}