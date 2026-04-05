package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.appointment.AppointmentStatus;
import com.aziz.demosec.dto.AppointmentDTO;
import com.aziz.demosec.dto.AppointmentRequest;
import com.aziz.demosec.dto.AppointmentResponse;
import com.aziz.demosec.dto.RescheduleRequest;
import com.aziz.demosec.service.IAppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final IAppointmentService appointmentService;
    private final ObjectMapper objectMapper;

    // In a real app we extract patientId from Security Principal
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse bookAppointment(
            @RequestParam(name = "patientId", required = false) Long patientId,
            @Valid @RequestBody AppointmentRequest request) {
        
        System.out.println("[DEBUG] Request reçue: " + request);

        Long idToUse = (patientId != null) ? patientId : request.getPatientId();
        if (idToUse == null) {
            throw new IllegalArgumentException("Patient ID must be provided either as query param or in request body");
        }
        System.out.println("[DEBUG] idToUse: " + idToUse);
        return appointmentService.bookAppointment(idToUse, request);
    }

    @GetMapping("/{appointmentId}")
    public AppointmentResponse getAppointment(@PathVariable("appointmentId") Long appointmentId) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    @GetMapping
    public List<AppointmentResponse> getAppointments(
            @RequestParam(name = "providerId", required = false) Long providerId,
            @RequestParam(name = "patientId", required = false) Long patientId,
            @RequestParam(name = "status", required = false) AppointmentStatus status,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return appointmentService.getAppointments(providerId, patientId, status, from, to);
    }

    @PostMapping("/{appointmentId}/cancel")
    public AppointmentResponse cancelAppointment(
            @PathVariable("appointmentId") Long appointmentId,
            @RequestParam(name = "userId", required = false) Long userId) {
        // Here we just pass userId for potential auditing
        return appointmentService.cancelAppointment(appointmentId, userId);
    }

    @PostMapping("/{appointmentId}/complete")
    public AppointmentResponse completeAppointment(
            @PathVariable("appointmentId") Long appointmentId,
            @RequestBody(required = false) String providerNotes) {
        // You could also accept a DTO for notes
        return appointmentService.completeAppointment(appointmentId, providerNotes);
    }

    @PostMapping("/{appointmentId}/reschedule")
    public AppointmentResponse rescheduleAppointment(
            @PathVariable("appointmentId") Long appointmentId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestBody RescheduleRequest request) {
        return appointmentService.rescheduleAppointment(appointmentId, request, userId);
    }

    @GetMapping("/patients/{patientId}/appointments")
    public ResponseEntity<List<AppointmentDTO>> getPatientAppointments(@PathVariable("patientId") Long patientId) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientId));
    }

    @GetMapping("/doctors/{doctorId}")
    public List<AppointmentResponse> getDoctorAppointments(
            @PathVariable("doctorId") Long doctorId,
            @RequestParam(name = "date", required = false) String date) {
        if (date != null) {
            return appointmentService.getDoctorAppointmentsByDate(doctorId, date);
        }
        return appointmentService.getAppointments(doctorId, null, null, null, null);
    }
    @PostMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirmAppointment(
            @PathVariable("id") Long id,
            @RequestBody(required = false) Object body) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(id));
    }

    @PostMapping("/{id}/start-live")
    public ResponseEntity<AppointmentResponse> startTeleconsultation(
            @PathVariable("id") Long id,
            @RequestBody(required = false) Object body) {
        return ResponseEntity.ok(appointmentService.startTeleconsultation(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable("id") Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
