package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.ProviderCalendar;
import com.aziz.demosec.Entities.WeeklySchedule;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.repository.*;
import com.aziz.demosec.security.jwt.JwtService;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduleController.class)
@AutoConfigureMockMvc(addFilters = false)
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private WeeklyScheduleRepository weeklyScheduleRepository;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private CalendarAvailabilityRepository calendarAvailabilityRepository;
    @MockitoBean private ProviderCalendarRepository providerCalendarRepository;
    @MockitoBean private ScheduleExceptionRepository scheduleExceptionRepository;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private User provider;
    private WeeklySchedule weeklySchedule;

    @BeforeEach
    void setUp() {
        provider = new User();
        provider.setId(1L);
        provider.setFullName("Provider Test");

        weeklySchedule = WeeklySchedule.builder()
                .id(1L)
                .provider(provider)
                .days(new ArrayList<>())
                .build();
    }

    @Test
    @WithMockUser
    void getWeeklySchedule_Success() throws Exception {
        when(weeklyScheduleRepository.findByProvider_Id(1L)).thenReturn(Optional.of(weeklySchedule));

        mockMvc.perform(get("/api/v1/providers/1/weekly-schedule"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void saveWeeklySchedule_Success() throws Exception {
        when(weeklyScheduleRepository.findByProvider_Id(1L)).thenReturn(Optional.of(weeklySchedule));
        when(userRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(weeklyScheduleRepository.save(any())).thenReturn(weeklySchedule);
        when(providerCalendarRepository.findByProvider_Id(anyLong())).thenReturn(Optional.of(new ProviderCalendar()));

        mockMvc.perform(put("/api/v1/providers/1/weekly-schedule")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(weeklySchedule)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getExceptions_Success() throws Exception {
        when(scheduleExceptionRepository.findByProviderId(1L)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/providers/1/schedule-exceptions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteException_Success() throws Exception {
        when(weeklyScheduleRepository.findByProvider_Id(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/v1/providers/1/schedule-exceptions/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
