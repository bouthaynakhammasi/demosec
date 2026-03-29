package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.appointment.AppointmentStatus;
import com.aziz.demosec.dto.AppointmentRequest;
import com.aziz.demosec.dto.AppointmentResponse;
import com.aziz.demosec.dto.RescheduleRequest;
import com.aziz.demosec.service.IAppointmentService;
import com.aziz.demosec.security.jwt.JwtService;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactiver les filtres pour simplifier le test d'API
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAppointmentService appointmentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private AppointmentRequest appointmentRequest;
    private AppointmentResponse appointmentResponse;

    @BeforeEach
    void setUp() {
        appointmentRequest = AppointmentRequest.builder()
                .patientId(1L)
                .doctorId(2L)
                .date("2026-04-01")
                .startTime("10:00")
                .endTime("10:30")
                .mode("ONLINE")
                .build();

        appointmentResponse = AppointmentResponse.builder()
                .id(1L)
                .patientId(1L)
                .providerId(2L)
                .status(AppointmentStatus.BOOKED)
                .build();
    }

    @Test
    @WithMockUser
    void bookAppointment_Success() throws Exception {
        when(appointmentService.bookAppointment(anyLong(), any(AppointmentRequest.class)))
                .thenReturn(appointmentResponse);

        mockMvc.perform(post("/api/v1/appointments")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAppointment_Success() throws Exception {
        when(appointmentService.getAppointmentById(anyLong())).thenReturn(appointmentResponse);

        mockMvc.perform(get("/api/v1/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void cancelAppointment_Success() throws Exception {
        when(appointmentService.cancelAppointment(anyLong(), anyLong())).thenReturn(appointmentResponse);

        mockMvc.perform(post("/api/v1/appointments/1/cancel")
                .with(csrf())
                .param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void completeAppointment_Success() throws Exception {
        when(appointmentService.completeAppointment(anyLong(), any())).thenReturn(appointmentResponse);

        mockMvc.perform(post("/api/v1/appointments/1/complete")
                .with(csrf())
                .content("Well done"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void rescheduleAppointment_Success() throws Exception {
        RescheduleRequest rescheduleRequest = new RescheduleRequest();
        rescheduleRequest.setNewDate("2026-04-02");
        rescheduleRequest.setNewStartTime("11:00");
        rescheduleRequest.setNewEndTime("11:30");
        rescheduleRequest.setNewMode("IN_PERSON");

        when(appointmentService.rescheduleAppointment(anyLong(), any(), anyLong()))
                .thenReturn(appointmentResponse);

        mockMvc.perform(post("/api/v1/appointments/1/reschedule")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rescheduleRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getPatientAppointments_Success() throws Exception {
        when(appointmentService.getPatientAppointments(anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/appointments/patients/1/appointments"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteAppointment_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/appointments/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
