package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.appointment.Appointment;
import com.aziz.demosec.Entities.appointment.AppointmentStatus;
import com.aziz.demosec.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByDoctorIdAndStartTimeBetween(
            Long doctorId, LocalDateTime start, LocalDateTime end
    );

    List<Appointment> findByPatientIdAndStartTimeBetween(
            Long patientId, LocalDateTime start, LocalDateTime end
    );

    List<Appointment> findByProviderIdAndStartTimeBetweenAndStatusNot(
            Long providerId, LocalDateTime start, LocalDateTime end, AppointmentStatus status
    );

    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.provider.id = :providerId")
    List<User> findDistinctPatientsByProviderId(@Param("providerId") Long providerId);
}
