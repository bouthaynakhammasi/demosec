package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.ProviderCalendar;
import com.aziz.demosec.Entities.ScheduleException;
import com.aziz.demosec.Entities.WeeklyDaySchedule;
import com.aziz.demosec.Entities.WeeklySchedule;
import com.aziz.demosec.Entities.WeeklyTimeSlot;
import com.aziz.demosec.Entities.appointment.CalendarAvailability;
import com.aziz.demosec.repository.CalendarAvailabilityRepository;
import com.aziz.demosec.repository.ProviderCalendarRepository;
import com.aziz.demosec.repository.ScheduleExceptionRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.repository.WeeklyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ScheduleController {

    private final WeeklyScheduleRepository weeklyScheduleRepository;
    private final UserRepository userRepository;
    private final CalendarAvailabilityRepository calendarAvailabilityRepository;
    private final ProviderCalendarRepository providerCalendarRepository;
    private final ScheduleExceptionRepository scheduleExceptionRepository;

    @GetMapping("/{providerId}/weekly-schedule")
    public ResponseEntity<WeeklySchedule> getWeeklySchedule(@PathVariable("providerId") Long providerId) {
        return weeklyScheduleRepository.findByProvider_Id(providerId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(
                        WeeklySchedule.builder()
                                .provider(userRepository.findById(providerId).orElseThrow())
                                .days(new ArrayList<>())
                                .build()
                ));
    }

    @PutMapping("/{providerId}/weekly-schedule")
    @Transactional
    public ResponseEntity<WeeklySchedule> saveWeeklySchedule(@PathVariable("providerId") Long providerId, @RequestBody WeeklySchedule schedule) {
        System.out.println("[DEBUG] Saving Weekly Schedule for Provider: " + providerId);
        
        WeeklySchedule existing = weeklyScheduleRepository.findByProvider_Id(providerId)
                .orElseGet(() -> {
                    System.out.println("[DEBUG] No existing schedule found, creating new one for provider: " + providerId);
                    WeeklySchedule ws = WeeklySchedule.builder()
                        .provider(userRepository.findById(providerId).orElseThrow(() -> new RuntimeException("User not found")))
                        .appointmentDuration(30)
                        .doctorId(providerId)
                        .build();
                    return weeklyScheduleRepository.save(ws);
                });

        // 1. Clear existing days (Hibernate will handle orphans if configured)
        existing.getDays().clear();
        
        // 2. Rebuild structure from scratch using the incoming payload
        if (schedule.getDays() != null) {
            for (WeeklyDaySchedule dayDto : schedule.getDays()) {
                WeeklyDaySchedule day = WeeklyDaySchedule.builder()
                        .weeklySchedule(existing)
                        .dayOfWeek(dayDto.getDayOfWeek())
                        .active(dayDto.isActive())
                        .enabled(dayDto.isActive())
                        .timeSlots(new java.util.ArrayList<>())
                        .build();
                
                if (dayDto.getTimeSlots() != null) {
                    for (WeeklyTimeSlot slotDto : dayDto.getTimeSlots()) {
                        WeeklyTimeSlot slot = WeeklyTimeSlot.builder()
                                .daySchedule(day)
                                .startTime(slotDto.getStartTime())
                                .endTime(slotDto.getEndTime())
                                .mode(slotDto.getMode())
                                .build();
                        day.getTimeSlots().add(slot);
                    }
                }
                existing.getDays().add(day);
            }
        }

        WeeklySchedule savedSchedule = weeklyScheduleRepository.save(existing);
        
        // Sync with calendar availabilities
        syncWithCalendarAvailabilities(providerId, savedSchedule);

        System.out.println("[DEBUG] Successfully saved schedule ID: " + savedSchedule.getId() + " - Days: " + savedSchedule.getDays().size());
        return ResponseEntity.ok(savedSchedule);
    }

    // --- Exceptions Endpoints ---

    @PutMapping("/{providerId}/specific-week")
    @Transactional
    public ResponseEntity<?> saveSpecificWeek(
            @PathVariable("providerId") Long providerId,
            @RequestParam(value = "startDate", required = false) String startDateParam,
            @RequestBody List<ScheduleException> weekDays) {
        
        System.out.println("[DEBUG] PUT /api/v1/providers/" + providerId + "/specific-week - startDateParam=" + startDateParam);
        System.out.println("[DEBUG] weekDays received: " + (weekDays != null ? weekDays.size() : "null"));

        try {
            LocalDate minDate = null;
            LocalDate maxDate = null;

            if (startDateParam != null) {
                minDate = LocalDate.parse(startDateParam);
                maxDate = minDate.plusDays(6);
            } else if (!weekDays.isEmpty()) {
                minDate = weekDays.get(0).getStartDate();
                maxDate = weekDays.get(0).getEndDate();
                for (ScheduleException e : weekDays) {
                    if (e.getStartDate().isBefore(minDate)) minDate = e.getStartDate();
                    if (e.getEndDate().isAfter(maxDate)) maxDate = e.getEndDate();
                }
            }
            
            if (minDate != null && maxDate != null) {
                System.out.println("[DEBUG] Range detected: " + minDate + " to " + maxDate);
                // Delete old "Semaine Spécifique" exceptions in this week range
                List<ScheduleException> existing = scheduleExceptionRepository.findByProviderId(providerId);
                for(ScheduleException ex : existing) {
                    if(!ex.getStartDate().isBefore(minDate) && !ex.getStartDate().isAfter(maxDate) && "Semaine Spécifique".equals(ex.getReason())) {
                        scheduleExceptionRepository.delete(ex);
                    }
                }
                scheduleExceptionRepository.flush(); // Ensure old are GONE before sync
            }
                
            // Save new
            if (weekDays != null) {
                for(ScheduleException e : weekDays) {
                    System.out.println("[DEBUG] Saving exception for date: " + e.getStartDate() + " - Available: " + e.isAvailable());
                    e.setProviderId(providerId);
                    e.setReason("Semaine Spécifique");
                    e.setType(com.aziz.demosec.Entities.ExceptionType.PARTIAL_AVAILABILITY);
                    if (e.getTimeSlots() != null) {
                        e.getTimeSlots().forEach(t -> {
                            t.setId(null);
                            t.setDaySchedule(null);
                            t.setScheduleException(e);
                        });
                    }
                    scheduleExceptionRepository.save(e);
                }
                scheduleExceptionRepository.flush(); // Commit new before sync
            }
            
            // Sync with calendar availabilities
            weeklyScheduleRepository.findByProvider_Id(providerId).ifPresent(templ -> {
                System.out.println("[DEBUG] Syncing calendar availabilities for provider: " + providerId);
                syncWithCalendarAvailabilities(providerId, templ);
            });
                
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to save specific week: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{providerId}/schedule-exceptions")
    public ResponseEntity<List<ScheduleException>> getExceptions(@PathVariable("providerId") Long providerId) {
        return ResponseEntity.ok(scheduleExceptionRepository.findByProviderId(providerId));
    }

    @PostMapping("/{providerId}/schedule-exceptions")
    @Transactional
    public ResponseEntity<ScheduleException> addException(@PathVariable("providerId") Long providerId, @RequestBody ScheduleException exception) {
        exception.setProviderId(providerId);
        if (exception.getTimeSlots() != null) {
            exception.getTimeSlots().forEach(slot -> slot.setScheduleException(exception));
        }
        ScheduleException saved = scheduleExceptionRepository.save(exception);
        
        weeklyScheduleRepository.findByProvider_Id(providerId).ifPresent(templ -> 
            syncWithCalendarAvailabilities(providerId, templ));
        
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{providerId}/schedule-exceptions/{id}")
    @Transactional
    public ResponseEntity<Void> deleteException(@PathVariable("providerId") Long providerId, @PathVariable("id") Long id) {
        scheduleExceptionRepository.deleteById(id);
        weeklyScheduleRepository.findByProvider_Id(providerId).ifPresent(templ -> 
            syncWithCalendarAvailabilities(providerId, templ));
        return ResponseEntity.noContent().build();
    }

    private void syncWithCalendarAvailabilities(Long providerId, WeeklySchedule template) {
        ProviderCalendar calendar = providerCalendarRepository.findByProvider_Id(providerId)
                .orElseGet(() -> providerCalendarRepository.save(ProviderCalendar.builder()
                        .provider(userRepository.findById(providerId).orElseThrow(() -> new RuntimeException("Provider not found")))
                        .build()));

        // Delete future slots starting from today that are still available (don't delete booked slots)
        calendarAvailabilityRepository.deleteByCalendar_IdAndStartTimeAfterAndStatus(
            calendar.getId(), 
            LocalDateTime.now().minusMinutes(5), // Buffer for current day
            com.aziz.demosec.Entities.appointment.AvailabilityStatus.AVAILABLE
        );

        List<ScheduleException> exceptions = scheduleExceptionRepository.findByProviderId(providerId);
        List<CalendarAvailability> slotsToSave = new ArrayList<>();

        // Generate next 30 days
        for (int i = 0; i < 30; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            String dayName = date.getDayOfWeek().name();

            // Check for exceptions first
            ScheduleException relevantException = exceptions.stream()
                .filter(e -> !date.isBefore(e.getStartDate()) && !date.isAfter(e.getEndDate()))
                .findFirst().orElse(null);

            if (relevantException != null) {
                if (!relevantException.isAvailable()) {
                    // It's a "Day Off", skip generating slots
                    continue;
                } else {
                    // Special working hours - use exception slots
                    generateSlotsForDate(calendar, date, relevantException.getTimeSlots(), slotsToSave);
                }
            } else {
                // Regular template
                template.getDays().stream()
                    .filter(d -> d.getDayOfWeek().equals(dayName) && d.isActive())
                    .findFirst()
                    .ifPresent(day -> generateSlotsForDate(calendar, date, day.getTimeSlots(), slotsToSave));
            }
        }
        
        if (!slotsToSave.isEmpty()) {
            calendarAvailabilityRepository.saveAll(slotsToSave);
        }
    }

    private void generateSlotsForDate(ProviderCalendar calendar, LocalDate date, List<WeeklyTimeSlot> templateSlots, List<CalendarAvailability> collector) {
        if (templateSlots == null) return;
        
        for (WeeklyTimeSlot ws : templateSlots) {
            try {
                LocalTime start = LocalTime.parse(ws.getStartTime());
                LocalTime end = LocalTime.parse(ws.getEndTime());
                
                CalendarAvailability slot = CalendarAvailability.builder()
                    .calendar(calendar)
                    .startTime(date.atTime(start))
                    .endTime(date.atTime(end))
                    .status(com.aziz.demosec.Entities.appointment.AvailabilityStatus.AVAILABLE)
                    .mode("ONLINE".equals(ws.getMode()) ? com.aziz.demosec.Entities.appointment.Mode.ONLINE : com.aziz.demosec.Entities.appointment.Mode.IN_PERSON)
                    .build();
                collector.add(slot);
            } catch (Exception e) {
                // Ignore parsing errors for individual slots
            }
        }
    }
}
