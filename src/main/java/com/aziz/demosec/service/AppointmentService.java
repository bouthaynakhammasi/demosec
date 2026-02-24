package com.aziz.demosec.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.aziz.demosec.Entities.Appointment;
import com.aziz.demosec.Entities.CalendarAvailability;
import com.aziz.demosec.repository.AppointmentRepository;
import com.aziz.demosec.repository.CalendarAvailabilityRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.AppointmentRequest;
import com.aziz.demosec.dto.AppointmentResponse;
import com.aziz.demosec.Mapper.AppointmentMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AppointmentService implements IAppointmentService {

    private AppointmentRepository appointmentRepository;
    private UserRepository userRepository;
    private CalendarAvailabilityRepository availabilityRepository;
    private AppointmentMapper appointmentMapper;

    @Override
    public AppointmentResponse addAppointment(AppointmentRequest request) {

        // fetch relations
        User patient = userRepository.findById(request.getPatientId()).orElse(null);
        User provider = userRepository.findById(request.getProviderId()).orElse(null);
        CalendarAvailability availability = availabilityRepository.findById(request.getAvailabilityId()).orElse(null);

        if (patient == null || provider == null || availability == null) {
            return null; // same "return null" style as your example
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .provider(provider)
                .availability(availability)
                .status(request.getStatus())
                .mode(request.getMode())
                .meetingLink(request.getMeetingLink())
                .visitAddress(request.getVisitAddress())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toDto(saved);
    }

    @Override
    public AppointmentResponse selectAppointmentByIdWithGet(Long id) {
        Appointment appointment = appointmentRepository.findById(id).get();
        return appointmentMapper.toDto(appointment);
    }

    @Override
    public AppointmentResponse selectAppointmentByIdWithOrElse(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return null;
        }
        return appointmentMapper.toDto(appointment);
    }

    @Override
    public List<AppointmentResponse> selectAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();

        List<AppointmentResponse> responses = new ArrayList<>();
        for (Appointment appointment : appointments) {
            responses.add(appointmentMapper.toDto(appointment));
        }
        return responses;
    }

    @Override
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return null;
        }

        // Update relations ONLY if new IDs provided
        if (request.getPatientId() != null) {
            User patient = userRepository.findById(request.getPatientId()).orElse(null);
            if (patient == null) return null;
            appointment.setPatient(patient);
        }

        if (request.getProviderId() != null) {
            User provider = userRepository.findById(request.getProviderId()).orElse(null);
            if (provider == null) return null;
            appointment.setProvider(provider);
        }

        if (request.getAvailabilityId() != null) {
            CalendarAvailability availability = availabilityRepository.findById(request.getAvailabilityId()).orElse(null);
            if (availability == null) return null;
            appointment.setAvailability(availability);
        }

        // update simple fields (only if provided)
        if (request.getStatus() != null) appointment.setStatus(request.getStatus());
        if (request.getMode() != null) appointment.setMode(request.getMode());
        if (request.getMeetingLink() != null) appointment.setMeetingLink(request.getMeetingLink());
        if (request.getVisitAddress() != null) appointment.setVisitAddress(request.getVisitAddress());

        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toDto(saved);
    }

    @Override
    public void deleteAppointmentById(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Override
    public void deleteAllAppointments() {
        appointmentRepository.deleteAll();
    }

    @Override
    public long countingAppointments() {
        return appointmentRepository.count();
    }

    @Override
    public boolean verifAppointmentById(Long id) {
        return appointmentRepository.existsById(id);
    }
}