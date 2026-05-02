package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByEventId(Long eventId);
    Optional<EventRegistration> findByEventIdAndParticipantId(Long eventId, Long participantId);
    long countByEventId(Long eventId);
}
