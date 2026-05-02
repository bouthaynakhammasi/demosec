package com.aziz.demosec.controller;

import com.aziz.demosec.dto.ProviderCalendarRequest;
import com.aziz.demosec.dto.ProviderCalendarResponse;
import com.aziz.demosec.service.IProviderCalendarService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProviderCalendarController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProviderCalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IProviderCalendarService calendarService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProviderCalendarRequest request;
    private ProviderCalendarResponse response;

    @BeforeEach
    void setUp() {
        request = new ProviderCalendarRequest();
        request.setProviderId(1L);

        response = new ProviderCalendarResponse();
        response.setId(1L);
        response.setProviderId(1L);
    }

    @Test
    @WithMockUser
    void addCalendar_Success() throws Exception {
        when(calendarService.addCalendar(any())).thenReturn(response);

        mockMvc.perform(post("/provider-calendar/add")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getCalendar_Success() throws Exception {
        when(calendarService.selectCalendarByIdWithGet(anyLong())).thenReturn(response);

        mockMvc.perform(get("/provider-calendar/get/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getAllCalendars_Success() throws Exception {
        when(calendarService.selectAllCalendars()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/provider-calendar/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateCalendar_Success() throws Exception {
        when(calendarService.updateCalendar(anyLong(), any())).thenReturn(response);

        mockMvc.perform(put("/provider-calendar/update/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteCalendar_Success() throws Exception {
        mockMvc.perform(delete("/provider-calendar/delete/1")
                .with(csrf()))
                .andExpect(status().isOk());
    }
}
