package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.PharmacyOrder;
import com.aziz.demosec.Entities.PharmacyOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacyOrderRepository extends JpaRepository<PharmacyOrder, Long> {

    List<PharmacyOrder> findByPatient_Id(Long patientId);

    List<PharmacyOrder> findByPharmacy_Id(Long pharmacyId);

    List<PharmacyOrder> findByStatus(PharmacyOrderStatus status);

    List<PharmacyOrder> findByPatient_IdAndStatus(Long patientId, PharmacyOrderStatus status);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE PharmacyOrder o SET o.status = :status, o.updatedAt = :updatedAt WHERE o.id = :id")
    void updateOrderStatus(@org.springframework.data.repository.query.Param("id") Long id, @org.springframework.data.repository.query.Param("status") PharmacyOrderStatus status, @org.springframework.data.repository.query.Param("updatedAt") java.time.LocalDateTime updatedAt);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE PharmacyOrder o SET o.status = :status, o.updatedAt = :updatedAt, o.pharmacistNote = :note WHERE o.id = :id")
    void updateOrderStatusWithNote(@org.springframework.data.repository.query.Param("id") Long id, @org.springframework.data.repository.query.Param("status") PharmacyOrderStatus status, @org.springframework.data.repository.query.Param("note") String note, @org.springframework.data.repository.query.Param("updatedAt") java.time.LocalDateTime updatedAt);
}
