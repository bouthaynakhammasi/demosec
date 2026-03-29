package com.aziz.demosec.controller;

import com.aziz.demosec.dto.patient.PatientRequestDTO;
import com.aziz.demosec.dto.patient.PatientResponseDTO;
import com.aziz.demosec.service.IPatientService;
import com.aziz.demosec.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IPatientService patientService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private com.aziz.demosec.security.CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private PatientResponseDTO patientResponseDTO;
    private PatientRequestDTO patientRequestDTO;

    @BeforeEach
    void setUp() {
        patientResponseDTO = new PatientResponseDTO();
        patientResponseDTO.setId(1L);
        patientResponseDTO.setFullName("Patient Zero");
        patientResponseDTO.setEmail("patient@example.com");

        patientRequestDTO = new PatientRequestDTO();
        patientRequestDTO.setFullName("Patient Zero");
        patientRequestDTO.setEmail("patient@example.com");
        patientRequestDTO.setPassword("password123");
    }

    @Test
    void create_ShouldReturnCreatedPatient() throws Exception {
        when(patientService.create(any(PatientRequestDTO.class))).thenReturn(patientResponseDTO);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDTO))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getById_ShouldReturnPatient() throws Exception {
        when(patientService.getById(1L)).thenReturn(patientResponseDTO);

        mockMvc.perform(get("/api/patients/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAll_ShouldReturnListOfPatients() throws Exception {
        when(patientService.getAll()).thenReturn(Collections.singletonList(patientResponseDTO));

        mockMvc.perform(get("/api/patients").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void update_ShouldReturnUpdatedPatient() throws Exception {
        when(patientService.update(anyLong(), any(PatientRequestDTO.class))).thenReturn(patientResponseDTO);

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(patientService).delete(1L);

        mockMvc.perform(delete("/api/patients/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void toggleEnabled_ShouldReturnNoContent() throws Exception {
        doNothing().when(patientService).toggleEnabled(1L);

        mockMvc.perform(patch("/api/patients/1/toggle").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
