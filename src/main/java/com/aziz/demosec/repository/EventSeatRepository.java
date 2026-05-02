package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.EventSeat;
import com.aziz.demosec.Entities.MedicalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {
        List<EventSeat> findByEventId(Long eventId);

        void deleteByEventId(Long eventId);
}
