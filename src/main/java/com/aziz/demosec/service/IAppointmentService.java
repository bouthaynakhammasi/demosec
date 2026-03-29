package com.aziz.demosec.service;

import com.aziz.demosec.Entities.appointment.AppointmentStatus;
import com.aziz.demosec.dto.AppointmentDTO;
import com.aziz.demosec.dto.AppointmentRequest;
import com.aziz.demosec.dto.AppointmentResponse;
import com.aziz.demosec.dto.RescheduleRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentService {

    AppointmentResponse bookAppointment(Long patientId, AppointmentRequest request);

    AppointmentResponse completeAppointment(Long appointmentId, String providerNotes);

    AppointmentResponse cancelAppointment(Long appointmentId, Long userId);

    AppointmentResponse rescheduleAppointment(Long oldAppointmentId, RescheduleRequest request, Long userId);

    AppointmentResponse getAppointmentById(Long id);

    List<AppointmentResponse> getAppointments(Long providerId, Long patientId, AppointmentStatus status, LocalDateTime from, LocalDateTime to);

    List<AppointmentDTO> getPatientAppointments(Long patientId);

    List<AppointmentResponse> getDoctorAppointmentsByDate(Long doctorId, String date);

    // New methods for Teleconsultation Flow
    AppointmentResponse confirmAppointment(Long appointmentId);
    
    AppointmentResponse startTeleconsultation(Long appointmentId);

    void deleteAppointment(Long appointmentId);
}
