package com.aziz.demosec.service;

import com.aziz.demosec.Entities.appointment.AvailabilityStatus;
import com.aziz.demosec.Entities.appointment.CalendarAvailability;
import com.aziz.demosec.Entities.ProviderCalendar;
import com.aziz.demosec.Mapper.CalendarAvailabilityMapper;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.CalendarAvailabilityRequest;
import com.aziz.demosec.dto.CalendarAvailabilityResponse;
import com.aziz.demosec.Entities.WeeklySchedule;
import com.aziz.demosec.Entities.WeeklyDaySchedule;
import com.aziz.demosec.Entities.WeeklyTimeSlot;
import com.aziz.demosec.Entities.ScheduleException;
import com.aziz.demosec.Entities.Doctor;
import com.aziz.demosec.Entities.appointment.Appointment;
import com.aziz.demosec.Entities.appointment.AppointmentStatus;
import com.aziz.demosec.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarAvailabilityService implements ICalendarAvailabilityService {

    private final CalendarAvailabilityRepository availabilityRepository;
    private final ProviderCalendarRepository providerCalendarRepository;
    private final CalendarAvailabilityMapper mapper;
    private final UserRepository userRepository;
    private final WeeklyScheduleRepository weeklyScheduleRepository;
    private final ScheduleExceptionRepository scheduleExceptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public ProviderCalendar getOrCreateCalendar(Long providerId) {
        return providerCalendarRepository.findByProvider_Id(providerId)
                .orElseGet(() -> {
                    User provider = userRepository.findById(providerId)
                            .orElseThrow(() -> new EntityNotFoundException("Provider not found"));
                    ProviderCalendar calendar = ProviderCalendar.builder().provider(provider).timezone("UTC").build();
                    return providerCalendarRepository.save(calendar);
                });
    }

    @Override
    @Transactional
    public CalendarAvailabilityResponse createAvailability(Long providerId, CalendarAvailabilityRequest request) {
        // We now store "manual" one-off slots as Special Hours Exceptions for consistency
        ScheduleException exception = ScheduleException.builder()
                .providerId(providerId)
                .startDate(request.getStartTime().toLocalDate())
                .endDate(request.getStartTime().toLocalDate())
                .isAvailable(true)
                .reason("Manual Slot")
                .timeSlots(List.of(WeeklyTimeSlot.builder()
                        .startTime(request.getStartTime().toLocalTime().toString())
                        .endTime(request.getEndTime().toLocalTime().toString())
                        .mode(request.getMode().name())
                        .build()))
                .build();
        scheduleExceptionRepository.save(exception);
        
        return CalendarAvailabilityResponse.builder()
                .providerId(providerId)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .mode(request.getMode())
                .status(AvailabilityStatus.AVAILABLE)
                .build();
    }

    @Override
    @Transactional
    public CalendarAvailabilityResponse updateAvailability(Long availabilityId, CalendarAvailabilityRequest request) {
        // For dynamic slots, update is mainly used for blocking/unblocking
        throw new UnsupportedOperationException("Dynamic slots updates are handled via Exceptions or Appointments");
    }

    @Override
    @Transactional
    public void deleteAvailability(Long availabilityId) {
        // Mainly for manual slots (Exceptions)
        scheduleExceptionRepository.deleteById(availabilityId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarAvailabilityResponse> getAvailabilities(Long providerId, LocalDateTime from, LocalDateTime to, AvailabilityStatus status) {
        if (from == null) from = LocalDateTime.now().minusDays(1); // include today/yesterday for context
        if (to == null) to = from.plusDays(30);

        // 1. Try to fetch real entities from the database first
        List<com.aziz.demosec.Entities.appointment.CalendarAvailability> existingSlots = availabilityRepository.findByCalendar_Provider_IdAndStartTimeBetween(providerId, from, to);
        
        if (!existingSlots.isEmpty()) {
            System.out.println("[DEBUG] Found " + existingSlots.size() + " real slots for provider " + providerId);
            return existingSlots.stream()
                .filter(s -> status == null || s.getStatus() == status)
                .map(s -> CalendarAvailabilityResponse.builder()
                        .id(s.getId()) // Crucial: provide the real ID for booking
                        .providerId(providerId)
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .mode(s.getMode())
                        .status(s.getStatus())
                        .build())
                .collect(java.util.stream.Collectors.toList());
        }

        // 2. Fallback to recalculating from template if no real slots exist (though they should have been synced)
        System.out.println("[DEBUG] No real slots found, recalculating from template for provider " + providerId);
        List<CalendarAvailabilityResponse> results = new ArrayList<>();
        
        WeeklySchedule template = weeklyScheduleRepository.findByProvider_Id(providerId).orElse(null);
        if (template == null) {
            return results;
        }

        int slotDuration = template.getAppointmentDuration() > 0 ? template.getAppointmentDuration() : 30;
        if (slotDuration < 15) slotDuration = 30; // Safety check

        final int finalSlotDuration = slotDuration;

        List<ScheduleException> exceptions = scheduleExceptionRepository.findByProviderId(providerId);
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndStartTimeBetween(providerId, from, to);

        LocalDateTime current = from;
        while (!current.isAfter(to)) {
            java.time.LocalDate date = current.toLocalDate();
            String dayName = date.getDayOfWeek().name();

            final java.time.LocalDate finalDate = date;
            final String finalDayName = dayName;
            
            ScheduleException relevantEx = exceptions.stream()
                    .filter(e -> !finalDate.isBefore(e.getStartDate()) && !finalDate.isAfter(e.getEndDate()))
                    .findFirst().orElse(null);

            if (relevantEx != null) {
                if (relevantEx.isAvailable()) {
                    // Specific override slots
                    generateSlots(results, providerId, finalDate, relevantEx.getTimeSlots(), appointments, finalSlotDuration);
                }
                // If isAvailable=false (Day Off), we generate nothing
            } else if (template != null && template.getDays() != null) {
                // Fallback to regular template
                template.getDays().stream()
                    .filter(d -> d.getDayOfWeek().equalsIgnoreCase(finalDayName) && d.isActive())
                    .findFirst()
                    .ifPresent(day -> generateSlots(results, providerId, finalDate, day.getTimeSlots(), appointments, finalSlotDuration));
            }
            
            current = current.plusDays(1).withHour(0).withMinute(0);
        }

        if (status != null) {
            return results.stream().filter(r -> r.getStatus() == status).collect(Collectors.toList());
        }
        return results;
    }

    private void generateSlots(List<CalendarAvailabilityResponse> results, Long providerId, java.time.LocalDate date, 
                               List<WeeklyTimeSlot> templateSlots, 
                               List<Appointment> appointments,
                               int slotDuration) {
        if (templateSlots == null) return;
        for (WeeklyTimeSlot ws : templateSlots) {
            try {
                java.time.LocalTime blockStart = java.time.LocalTime.parse(ws.getStartTime());
                java.time.LocalTime blockEnd = java.time.LocalTime.parse(ws.getEndTime());

                java.time.LocalTime current = blockStart;
                while (!current.plusMinutes(slotDuration).isAfter(blockEnd)) {
                    LocalDateTime start = date.atTime(current);
                    LocalDateTime end = date.atTime(current.plusMinutes(slotDuration));

                    // Check if any booking starts at exactly this time
                    boolean isBooked = appointments.stream()
                            .anyMatch(a -> a.getStartTime().equals(start));

                    results.add(CalendarAvailabilityResponse.builder()
                            .providerId(providerId)
                            .startTime(start)
                            .endTime(end)
                            .mode("ONLINE".equals(ws.getMode()) ? com.aziz.demosec.Entities.appointment.Mode.ONLINE : com.aziz.demosec.Entities.appointment.Mode.IN_PERSON)
                            .status(isBooked ? AvailabilityStatus.BOOKED : AvailabilityStatus.AVAILABLE)
                            .build());
                    
                    current = current.plusMinutes(slotDuration);
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to parse slot for provider " + providerId + ": " + e.getMessage());
            }
        }
    }
}
