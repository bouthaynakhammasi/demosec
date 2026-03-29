package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.Clinic;
import com.aziz.demosec.service.IClinicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ClinicControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IClinicService clinicService;

    @InjectMocks
    private ClinicController clinicController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(clinicController)
                .build();
    }

    @Test
    public void testGetAllClinics() throws Exception {
        Clinic c1 = Clinic.builder().id(1L).name("Clinic A").build();
        Clinic c2 = Clinic.builder().id(2L).name("Clinic B").build();

        when(clinicService.getAllClinics()).thenReturn(Arrays.asList(c1, c2));

        mockMvc.perform(get("/api/clinics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Clinic A")));
    }

    @Test
    public void testGetClinicById_Success() throws Exception {
        Clinic c1 = Clinic.builder().id(10L).name("Clinic A").build();

        when(clinicService.getClinicById(10L)).thenReturn(Optional.of(c1));

        mockMvc.perform(get("/api/clinics/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Clinic A")));
    }

    @Test
    public void testGetClinicById_NotFound() throws Exception {
        when(clinicService.getClinicById(10L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clinics/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateClinic() throws Exception {
        Clinic req = Clinic.builder().name("New Clinic").build();
        Clinic res = Clinic.builder().id(5L).name("New Clinic").build();
        
        when(clinicService.createClinic(any(Clinic.class))).thenReturn(res);

        mockMvc.perform(post("/api/clinics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.name", is("New Clinic")));
    }

    @Test
    public void testDeleteClinic() throws Exception {
        doNothing().when(clinicService).deleteClinic(5L);

        mockMvc.perform(delete("/api/clinics/5"))
                .andExpect(status().isOk());
    }
}
