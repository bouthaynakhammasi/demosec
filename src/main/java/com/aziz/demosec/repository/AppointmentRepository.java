package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Appointment;
import com.aziz.demosec.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.provider.id = :providerId")
    List<User> findDistinctPatientsByProviderId(@Param("providerId") Long providerId);
}
