package com.aziz.demosec.service;

import com.aziz.demosec.Entities.appointment.Appointment;
import com.aziz.demosec.Entities.appointment.AppointmentStatus;
import com.aziz.demosec.Entities.appointment.Mode;
import com.aziz.demosec.Mapper.AppointmentMapper;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.AppointmentRequest;
import com.aziz.demosec.dto.AppointmentResponse;
import com.aziz.demosec.dto.RescheduleRequest;
import com.aziz.demosec.repository.AppointmentRepository;
import com.aziz.demosec.repository.CalendarAvailabilityRepository;
import com.aziz.demosec.repository.DoctorRepository;
import com.aziz.demosec.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private CalendarAvailabilityRepository availabilityRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private User patient;
    private User doctor;
    private Appointment appointment;
    private AppointmentRequest appointmentRequest;

    @BeforeEach
    void setUp() {
        patient = new User();
        patient.setId(1L);
        patient.setFullName("Patient Test");

        doctor = new User();
        doctor.setId(2L);
        doctor.setFullName("Doctor Test");

        appointment = Appointment.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(2L)
                .providerId(2L)
                .status(AppointmentStatus.BOOKED)
                .startTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0)))
                .endTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 30)))
                .build();

        appointmentRequest = AppointmentRequest.builder()
                .doctorId(2L)
                .date(LocalDate.now().plusDays(1).toString())
                .startTime("10:00")
                .endTime("10:30")
                .mode("ONLINE")
                .notes("Test notes")
                .build();
    }

    @Test
    void bookAppointment_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByProviderIdAndStartTimeBetweenAndStatusNot(anyLong(), any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(availabilityRepository.findByCalendar_Provider_IdAndStartTimeBetween(anyLong(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toDto(any(Appointment.class))).thenReturn(new AppointmentResponse());

        AppointmentResponse response = appointmentService.bookAppointment(1L, appointmentRequest);

        assertNotNull(response);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_PatientNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
                appointmentService.bookAppointment(1L, appointmentRequest));
    }

    @Test
    void bookAppointment_Conflict() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByProviderIdAndStartTimeBetweenAndStatusNot(anyLong(), any(), any(), any()))
                .thenReturn(List.of(new Appointment()));

        assertThrows(IllegalStateException.class, () -> 
                appointmentService.bookAppointment(1L, appointmentRequest));
    }

    @Test
    void completeAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toDto(any(Appointment.class))).thenReturn(new AppointmentResponse());

        AppointmentResponse response = appointmentService.completeAppointment(1L, "Notes");

        assertNotNull(response);
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
    }

    @Test
    void cancelAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(availabilityRepository.findByCalendar_Provider_IdAndStartTimeBetween(anyLong(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toDto(any(Appointment.class))).thenReturn(new AppointmentResponse());

        AppointmentResponse response = appointmentService.cancelAppointment(1L, 1L);

        assertNotNull(response);
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
    }

    @Test
    void cancelAppointment_InvalidStatus() {
        appointment.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThrows(IllegalStateException.class, () -> 
                appointmentService.cancelAppointment(1L, 1L));
    }

    @Test
    void rescheduleAppointment_Success() {
        RescheduleRequest rescheduleRequest = new RescheduleRequest();
        rescheduleRequest.setNewDate(LocalDate.now().plusDays(2).toString());
        rescheduleRequest.setNewStartTime("11:00");
        rescheduleRequest.setNewEndTime("11:30");
        rescheduleRequest.setNewMode("IN_PERSON");

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        // Mock bookAppointment part
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByProviderIdAndStartTimeBetweenAndStatusNot(anyLong(), any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(availabilityRepository.findByCalendar_Provider_IdAndStartTimeBetween(anyLong(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toDto(any(Appointment.class))).thenReturn(new AppointmentResponse());

        AppointmentResponse response = appointmentService.rescheduleAppointment(1L, rescheduleRequest, 1L);

        assertNotNull(response);
        assertEquals(AppointmentStatus.RESCHEDULED, appointment.getStatus());
    }

    @Test
    void getAppointmentById_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toDto(any(Appointment.class))).thenReturn(new AppointmentResponse());

        AppointmentResponse response = appointmentService.getAppointmentById(1L);

        assertNotNull(response);
    }
}
