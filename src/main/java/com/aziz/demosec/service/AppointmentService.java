package com.aziz.demosec.service;

import com.aziz.demosec.Entities.appointment.Appointment;
import com.aziz.demosec.Entities.appointment.AppointmentStatus;
import com.aziz.demosec.Entities.appointment.Mode;
import com.aziz.demosec.Mapper.AppointmentMapper;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.Entities.Doctor;
import com.aziz.demosec.dto.AppointmentDTO;
import com.aziz.demosec.dto.AppointmentRequest;
import com.aziz.demosec.dto.AppointmentResponse;
import com.aziz.demosec.dto.RescheduleRequest;
import com.aziz.demosec.repository.AppointmentRepository;
import com.aziz.demosec.repository.DoctorRepository;
import com.aziz.demosec.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentMapper appointmentMapper;
    private final com.aziz.demosec.repository.CalendarAvailabilityRepository availabilityRepository;

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(Long patientId, AppointmentRequest request) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        Long doctorId = request.getDoctorId() != null ? request.getDoctorId() : request.getProviderId();
        User provider = userRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found"));

        LocalDateTime start = LocalDateTime.of(LocalDate.parse(request.getDate()), LocalTime.parse(request.getStartTime()));
        LocalDateTime end = LocalDateTime.of(LocalDate.parse(request.getDate()), LocalTime.parse(request.getEndTime()));
        Mode mode = Mode.valueOf(request.getMode());

        // FIX: Empêcher le double booking (vérifier qu'aucun RDV non-annulé n'existe à la même heure)
        List<Appointment> conflicts = appointmentRepository.findByProviderIdAndStartTimeBetweenAndStatusNot(
                provider.getId(), start.minusSeconds(1), start.plusSeconds(1), AppointmentStatus.CANCELLED);

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Ce créneau est déjà réservé par un autre patient.");
        }

        String meetingLink = null;
        String visitAddress = null;

        if (mode == Mode.ONLINE) {
            meetingLink = "https://meet.jit.si/" + UUID.randomUUID().toString();
        } else if (mode == Mode.IN_PERSON) {
            visitAddress = "Medical Center";
        }

        Appointment appointment = Appointment.builder()
                .patientId(patient.getId())
                .patient(patient)
                .doctorId(provider.getId())
                .doctor(provider)
                .providerId(provider.getId())
                .provider(provider)
                .startTime(start)
                .endTime(end)
                .status(AppointmentStatus.BOOKED)
                .meetingLink(meetingLink)
                .visitAddress(visitAddress)
                .patientNotes(request.getNotes())
                .build();

        // Find the slot to link it to the appointment and mark it as BOOKED
        availabilityRepository.findByCalendar_Provider_IdAndStartTimeBetween(provider.getId(), start.minusSeconds(1), start.plusSeconds(1))
                .forEach(slot -> {
                    appointment.setAvailabilityId(slot.getId()); // Set the required availability_id
                    slot.setStatus(com.aziz.demosec.Entities.appointment.AvailabilityStatus.BOOKED);
                    availabilityRepository.save(slot);
                    System.out.println("[DEBUG] Slot " + slot.getId() + " marked as BOOKED for provider " + provider.getId());
                });

        // Use a fallback for availabilityId if not found, to avoid the 500 error IF database strictly enforces it
        if (appointment.getAvailabilityId() == null) {
            System.err.println("[WARN] No slot found for appointment at " + start + " for provider " + provider.getId());
            // Optionally: throw new IllegalStateException("Slot not found");
        }

        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse completeAppointment(Long appointmentId, String providerNotes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setCompletedAt(LocalDateTime.now());
        appointment.setProviderNotes(providerNotes);

        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.BOOKED && appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only BOOKED or CONFIRMED appointments can be cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledAt(LocalDateTime.now());

        // [MODIF] Libérer le créneau dans le calendrier des disponibilités
        availabilityRepository.findByCalendar_Provider_IdAndStartTimeBetween(appointment.getProviderId(),
                        appointment.getStartTime().minusSeconds(1), appointment.getStartTime().plusSeconds(1))
                .forEach(slot -> {
                    slot.setStatus(com.aziz.demosec.Entities.appointment.AvailabilityStatus.AVAILABLE);
                    availabilityRepository.save(slot);
                });

        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse rescheduleAppointment(Long oldAppointmentId, RescheduleRequest request, Long userId) {
        Appointment oldAppointment = appointmentRepository.findById(oldAppointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Old Appointment not found"));

        if (oldAppointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new IllegalStateException("Only BOOKED appointments can be rescheduled");
        }

        oldAppointment.setStatus(AppointmentStatus.RESCHEDULED);

        AppointmentRequest newRequest = AppointmentRequest.builder()
                .providerId(request.getNewProviderId() != null ? request.getNewProviderId() : oldAppointment.getProviderId())
                .date(request.getNewDate())
                .startTime(request.getNewStartTime())
                .endTime(request.getNewEndTime())
                .mode(request.getNewMode())
                .notes(oldAppointment.getPatientNotes())
                .build();

        return bookAppointment(oldAppointment.getPatientId(), newRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        return appointmentMapper.toDto(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointments(Long providerId, Long patientId, AppointmentStatus status, LocalDateTime from, LocalDateTime to) {
        List<Appointment> results;

        if (patientId != null) {
            results = appointmentRepository.findByPatientId(patientId);
        } else if (providerId != null) {
            results = appointmentRepository.findByDoctorId(providerId);
        } else {
            results = appointmentRepository.findAll();
        }

        if (status != null) {
            results = results.stream()
                    .filter(a -> a.getStatus() == status)
                    .collect(Collectors.toList());
        }

        if (from != null && to != null) {
            results = results.stream()
                    .filter(a -> {
                        LocalDateTime start = a.getStartTime();
                        return (start != null) && (start.isEqual(from) || start.isAfter(from)) &&
                                (start.isEqual(to) || start.isBefore(to));
                    })
                    .collect(Collectors.toList());
        }

        return results.stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(appt -> {
                    AppointmentDTO dto = new AppointmentDTO();
                    dto.setId(appt.getId());
                    if (appt.getStartTime() != null) {
                        dto.setDate(appt.getStartTime().toLocalDate().toString());
                        dto.setStartTime(appt.getStartTime().toLocalTime().toString().substring(0, 5));
                    }
                    if (appt.getEndTime() != null) {
                        dto.setEndTime(appt.getEndTime().toLocalTime().toString().substring(0, 5));
                    }
                    if (appt.getStatus() != null) {
                        dto.setStatus(appt.getStatus().name());
                    }
                    if (appt.getPatientNotes() != null) {
                        dto.setNotes(appt.getPatientNotes());
                    }
                    // Extract meeting/mode logic if mode exists in Appointment, else we guess
                    // By checking how appointment is built, we have Mode? Actually Appointment entity doesn't have mode, it has meetingLink/visitAddress.
                    if (appt.getMeetingLink() != null || appt.getStatus() == AppointmentStatus.LIVE) {
                        dto.setMode("ONLINE");
                        dto.setMeetingLink(appt.getMeetingLink());
                    } else {
                        dto.setMode("IN_PERSON");
                    }

                    if (appt.getPatient() != null) {
                        dto.setPatientName(appt.getPatient().getFullName());
                    } else {
                        userRepository.findById(appt.getPatientId()).ifPresent(p -> dto.setPatientName(p.getFullName()));
                    }

                    dto.setDoctorId(appt.getDoctorId());

                    User doctor = appt.getDoctor();
                    if (doctor == null) {
                        doctor = userRepository.findById(appt.getDoctorId()).orElse(null);
                    }

                    if (doctor != null) {
                        dto.setDoctorName(doctor.getFullName());

                        String photo = doctor.getProfileImage();

                        Doctor doctorProfile = doctorRepository.findById(appt.getDoctorId()).orElse(null);
                        if (doctorProfile != null) {
                            if (photo == null || photo.isEmpty()) {
                                photo = doctorProfile.getProfileImage();
                            }
                            dto.setDoctorSpecialty(doctorProfile.getSpecialty());
                            if (doctorProfile.getClinic() != null) {
                                dto.setClinicName(doctorProfile.getClinic().getName());
                                dto.setClinicAddress(doctorProfile.getClinic().getAddress() != null ? doctorProfile.getClinic().getAddress() : "");
                            }
                        }
                        dto.setDoctorProfilePicture(photo);
                    }
                    dto.setMeetingLink(appt.getMeetingLink());
                    if (appt.getStatus() != null) {
                        dto.setStatus(appt.getStatus().name());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getDoctorAppointmentsByDate(Long doctorId, String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

        return appointmentRepository.findByDoctorIdAndStartTimeBetween(doctorId, startOfDay, endOfDay).stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentResponse confirmAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse startTeleconsultation(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (appointment.getMeetingLink() == null || appointment.getMeetingLink().trim().isEmpty()) {
            // Utilisation de 8x8.vc (Jitsi Powered) pour éviter les blocages de modérateur meet.jit.si
            String roomName = "MedicareApp_" + appointmentId + "_" + UUID.randomUUID().toString().substring(0, 8);
            appointment.setMeetingLink("https://8x8.vc/vpaas-magic-cookie-ca9c470125cc442fafde76f9ba7b4474/" + roomName);
        }

        appointment.setStatus(AppointmentStatus.LIVE);
        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }
    @Override
    @Transactional
    public void deleteAppointment(Long appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }
}