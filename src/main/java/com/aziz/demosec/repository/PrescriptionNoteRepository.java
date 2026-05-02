package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.PrescriptionNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionNoteRepository extends JpaRepository<PrescriptionNote, Long> {

    List<PrescriptionNote> findByOrder_Id(Long orderId);
}
