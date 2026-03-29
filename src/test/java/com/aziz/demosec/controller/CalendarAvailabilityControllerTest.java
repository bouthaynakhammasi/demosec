package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.appointment.AvailabilityStatus;
import com.aziz.demosec.dto.CalendarAvailabilityRequest;
import com.aziz.demosec.dto.CalendarAvailabilityResponse;
import com.aziz.demosec.service.ICalendarAvailabilityService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalendarAvailabilityController.class)
@AutoConfigureMockMvc(addFilters = false)
class CalendarAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ICalendarAvailabilityService availabilityService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private CalendarAvailabilityRequest request;
    private CalendarAvailabilityResponse response;

    @BeforeEach
    void setUp() {
        request = new CalendarAvailabilityRequest();
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(1).plusMinutes(30));

        response = CalendarAvailabilityResponse.builder()
                .providerId(1L)
                .status(AvailabilityStatus.AVAILABLE)
                .build();
    }

    @Test
    @WithMockUser
    void addAvailability_Success() throws Exception {
        when(availabilityService.createAvailability(anyLong(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/providers/1/availabilities")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void getAvailabilities_Success() throws Exception {
        when(availabilityService.getAvailabilities(anyLong(), any(), any(), any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/providers/1/availabilities"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateAvailability_Success() throws Exception {
        when(availabilityService.updateAvailability(anyLong(), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/availabilities/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteAvailability_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/availabilities/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
