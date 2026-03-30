package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.Entities.appointment.*;
import com.aziz.demosec.Mapper.CalendarAvailabilityMapper;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.CalendarAvailabilityRequest;
import com.aziz.demosec.dto.CalendarAvailabilityResponse;
import com.aziz.demosec.repository.*;
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
class CalendarAvailabilityServiceTest {

    @Mock
    private CalendarAvailabilityRepository availabilityRepository;
    @Mock
    private ProviderCalendarRepository providerCalendarRepository;
    @Mock
    private CalendarAvailabilityMapper mapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WeeklyScheduleRepository weeklyScheduleRepository;
    @Mock
    private ScheduleExceptionRepository scheduleExceptionRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private CalendarAvailabilityService availabilityService;

    private User provider;
    private ProviderCalendar calendar;

    @BeforeEach
    void setUp() {
        provider = new User();
        provider.setId(1L);
        provider.setFullName("Provider Test");

        calendar = ProviderCalendar.builder()
                .id(1L)
                .provider(provider)
                .timezone("UTC")
                .build();
    }

    @Test
    void getOrCreateCalendar_Existing() {
        when(providerCalendarRepository.findByProvider_Id(1L)).thenReturn(Optional.of(calendar));

        ProviderCalendar result = availabilityService.getOrCreateCalendar(1L);

        assertEquals(calendar, result);
        verify(providerCalendarRepository, never()).save(any());
    }

    @Test
    void getOrCreateCalendar_New() {
        when(providerCalendarRepository.findByProvider_Id(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(providerCalendarRepository.save(any())).thenReturn(calendar);

        ProviderCalendar result = availabilityService.getOrCreateCalendar(1L);

        assertNotNull(result);
        verify(providerCalendarRepository, times(1)).save(any());
    }

    @Test
    void createAvailability_Success() {
        CalendarAvailabilityRequest request = new CalendarAvailabilityRequest();
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(1).plusMinutes(30));
        request.setMode(Mode.ONLINE);

        CalendarAvailabilityResponse response = availabilityService.createAvailability(1L, request);

        assertNotNull(response);
        assertEquals(AvailabilityStatus.AVAILABLE, response.getStatus());
        verify(scheduleExceptionRepository, times(1)).save(any(ScheduleException.class));
    }

    @Test
    void getAvailabilities_NormalTemplate() {
        Long providerId = 1L;
        LocalDate date = LocalDate.now().plusDays(1);
        String dayOfWeek = date.getDayOfWeek().name();

        // Template setup
        WeeklyTimeSlot slot = WeeklyTimeSlot.builder()
                .startTime("09:00")
                .endTime("10:00")
                .mode("ONLINE")
                .build();
        WeeklyDaySchedule daySchedule = WeeklyDaySchedule.builder()
                .dayOfWeek(dayOfWeek)
                .active(true)
                .enabled(true)
                .timeSlots(List.of(slot))
                .build();
        WeeklySchedule template = WeeklySchedule.builder()
                .provider(provider)
                .days(List.of(daySchedule))
                .build();

        when(weeklyScheduleRepository.findByProvider_Id(providerId)).thenReturn(Optional.of(template));
        when(scheduleExceptionRepository.findByProviderId(providerId)).thenReturn(new ArrayList<>());
        when(appointmentRepository.findByProviderIdAndStartTimeBetweenAndStatusNot(anyLong(), any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(doctorRepository.findById(providerId)).thenReturn(Optional.empty()); // Default 30 min duration

        List<CalendarAvailabilityResponse> results = availabilityService.getAvailabilities(providerId, date.atStartOfDay(), date.atTime(23, 59), null);

        // 09:00 to 10:00 with 30 min duration -> 2 slots
        assertEquals(2, results.size());
        assertEquals(AvailabilityStatus.AVAILABLE, results.get(0).getStatus());
    }

    @Test
    void getAvailabilities_WithException_DayOff() {
        Long providerId = 1L;
        LocalDate date = LocalDate.now().plusDays(1);

        ScheduleException exception = ScheduleException.builder()
                .startDate(date)
                .endDate(date)
                .isAvailable(false) // Day off
                .build();

        when(scheduleExceptionRepository.findByProviderId(providerId)).thenReturn(List.of(exception));
        when(weeklyScheduleRepository.findByProvider_Id(providerId)).thenReturn(Optional.empty());
        when(appointmentRepository.findByProviderIdAndStartTimeBetweenAndStatusNot(anyLong(), any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(doctorRepository.findById(providerId)).thenReturn(Optional.empty());

        List<CalendarAvailabilityResponse> results = availabilityService.getAvailabilities(providerId, date.atStartOfDay(), date.atTime(23, 59), null);

        assertEquals(0, results.size());
    }

    @Test
    void getAvailabilities_WithBookedAppointment() {
        Long providerId = 1L;
        LocalDate date = LocalDate.now().plusDays(1);
        String dayOfWeek = date.getDayOfWeek().name();
        
        LocalDateTime startTIme = date.atTime(9, 0);

        // Template setup
        WeeklyTimeSlot slot = WeeklyTimeSlot.builder().startTime("09:00").endTime("09:30").mode("ONLINE").build();
        WeeklyDaySchedule daySchedule = WeeklyDaySchedule.builder().dayOfWeek(dayOfWeek).active(true).enabled(true).timeSlots(List.of(slot)).build();
        WeeklySchedule template = WeeklySchedule.builder().provider(provider).days(List.of(daySchedule)).build();

        // Appointment at 09:00
        Appointment app = Appointment.builder().providerId(providerId).startTime(startTIme).status(AppointmentStatus.BOOKED).build();

        when(weeklyScheduleRepository.findByProvider_Id(providerId)).thenReturn(Optional.of(template));
        when(scheduleExceptionRepository.findByProviderId(providerId)).thenReturn(new ArrayList<>());
        when(appointmentRepository.findByProviderIdAndStartTimeBetweenAndStatusNot(anyLong(), any(), any(), any()))
                .thenReturn(List.of(app));
        when(doctorRepository.findById(providerId)).thenReturn(Optional.empty());

        List<CalendarAvailabilityResponse> results = availabilityService.getAvailabilities(providerId, date.atStartOfDay(), date.atTime(23, 59), null);

        assertEquals(1, results.size());
        assertEquals(AvailabilityStatus.BOOKED, results.get(0).getStatus());
    }
}
