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
        if (from == null) from = LocalDateTime.now();
        if (to == null) to = from.plusDays(30);

        List<CalendarAvailabilityResponse> results = new java.util.ArrayList<>();
        
        WeeklySchedule template = weeklyScheduleRepository.findByProvider_Id(providerId).orElse(null);
        List<ScheduleException> exceptions = scheduleExceptionRepository.findByProviderId(providerId);
        List<Appointment> appointments = appointmentRepository.findByProviderIdAndStartTimeBetweenAndStatusNot(
                providerId, from, to, AppointmentStatus.CANCELLED);

        // Fetch doctor's custom slot duration or default to 30
        int slotDuration = doctorRepository.findById(providerId)
                .map(d -> d.getSlotDuration() != null && d.getSlotDuration() > 0 ? d.getSlotDuration() : 30)
                .orElse(30);

        LocalDateTime current = from;
        while (!current.isAfter(to)) {
            java.time.LocalDate date = current.toLocalDate();
            String dayOfWeekName = date.getDayOfWeek().name();

            // 1. Check exceptions (Holidays / Special Hours)
            ScheduleException relevantEx = exceptions == null ? null : exceptions.stream()
                    .filter(e -> e.getStartDate() != null && e.getEndDate() != null)
                    .filter(e -> !date.isBefore(e.getStartDate()) && !date.isAfter(e.getEndDate()))
                    .min(java.util.Comparator.<ScheduleException>comparingLong(e -> 
                            java.time.temporal.ChronoUnit.DAYS.between(e.getStartDate(), e.getEndDate()))
                            .thenComparing(java.util.Comparator.comparing(ScheduleException::getId).reversed()))
                    .orElse(null);

            if (relevantEx != null) {
                if (relevantEx.isAvailable()) {
                    // Specific override slots
                    generateSlots(results, providerId, date, relevantEx.getTimeSlots(), appointments, slotDuration);
                }
                // If isAvailable=false (Day Off), we generate nothing
            } else if (template != null && template.getDays() != null) {
                // Fallback to regular template
                template.getDays().stream()
                    .filter(d -> d != null && d.getDayOfWeek() != null && d.getDayOfWeek().equalsIgnoreCase(dayOfWeekName) && (d.isActive() || d.isEnabled()))
                    .findFirst()
                    .ifPresent(day -> generateSlots(results, providerId, date, day.getTimeSlots(), appointments, slotDuration));
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
        if (templateSlots == null || slotDuration <= 0) return;

        for (WeeklyTimeSlot ws : templateSlots) {
            if (ws.getStartTime() == null || ws.getEndTime() == null) continue;
            
            try {
                java.time.LocalTime blockStart = java.time.LocalTime.parse(ws.getStartTime());
                java.time.LocalTime blockEnd = java.time.LocalTime.parse(ws.getEndTime());

                java.time.LocalTime current = blockStart;
                while (!current.plusMinutes(slotDuration).isAfter(blockEnd)) {
                    LocalDateTime start = date.atTime(current);
                    LocalDateTime end = date.atTime(current.plusMinutes(slotDuration));

                    // Check if any booking starts at exactly this time
                    boolean isBooked = appointments != null && appointments.stream()
                            .anyMatch(a -> a.getProviderId() != null && a.getProviderId().equals(providerId) && 
                                          a.getStartTime() != null && a.getStartTime().equals(start));

                    results.add(CalendarAvailabilityResponse.builder()
                            .providerId(providerId)
                            .startTime(start)
                            .endTime(end)
                            .mode("ONLINE".equals(ws.getMode()) ? com.aziz.demosec.Entities.appointment.Mode.ONLINE : com.aziz.demosec.Entities.appointment.Mode.IN_PERSON)
                            .status(isBooked ? AvailabilityStatus.BOOKED : AvailabilityStatus.AVAILABLE)
                            .build());
                    
                    current = current.plusMinutes(slotDuration);
                    if (current.equals(java.time.LocalTime.MIDNIGHT)) break; // Prevent infinite loop if slot duration wraps around
                }
            } catch (Exception e) {
                // Log and skip invalid slots
                System.err.println("[ERROR] Failed to generate slots for slot ID " + ws.getId() + ": " + e.getMessage());
            }
        }
    }
}
