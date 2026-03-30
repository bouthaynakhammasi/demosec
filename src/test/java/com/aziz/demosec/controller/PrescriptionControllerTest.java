package com.aziz.demosec.controller;

import com.aziz.demosec.dto.PrescriptionRequest;
import com.aziz.demosec.dto.PrescriptionResponse;
import com.aziz.demosec.service.IPrescriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrescriptionController.class)
@AutoConfigureMockMvc(addFilters = false)
class PrescriptionControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPrescriptionService prescriptionService;

    @Autowired
    private ObjectMapper objectMapper;

    private PrescriptionRequest request;
    private PrescriptionResponse response;

    @BeforeEach
    void setUp() {
        request = PrescriptionRequest.builder()
                .consultationId(1L)
                .date("2026-03-28")
                .medication("Paracetamol")
                .dosage("500mg")
                .instructions("3 times a day")
                .build();

        response = PrescriptionResponse.builder()
                .id(1L)
                .build();
    }

    @Test
    @WithMockUser
    void add_ShouldReturnOk() throws Exception {
        when(prescriptionService.addPrescription(any(PrescriptionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/prescription/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getById_ShouldReturnPrescription() throws Exception {
        when(prescriptionService.selectPrescriptionByIdWithGet(1L)).thenReturn(response);

        mockMvc.perform(get("/prescription/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturnList() throws Exception {
        when(prescriptionService.selectAllPrescriptions()).thenReturn(List.of(response));

        mockMvc.perform(get("/prescription/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void update_ShouldReturnUpdatedPrescription() throws Exception {
        when(prescriptionService.updatePrescription(eq(1L), any(PrescriptionRequest.class))).thenReturn(response);

        mockMvc.perform(put("/prescription/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/prescription/delete/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
