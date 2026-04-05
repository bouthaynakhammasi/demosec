package com.aziz.demosec.controller;

import com.aziz.demosec.dto.DiagnosisRequest;
import com.aziz.demosec.dto.DiagnosisResponse;
import com.aziz.demosec.service.IDiagnosisService;
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

@WebMvcTest(DiagnosisController.class)
@AutoConfigureMockMvc(addFilters = false)
class DiagnosisControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDiagnosisService diagnosisService;

    @Autowired
    private ObjectMapper objectMapper;

    private DiagnosisRequest request;
    private DiagnosisResponse response;

    @BeforeEach
    void setUp() {
        request = DiagnosisRequest.builder()
                .consultationId(1L)
                .description("Test Diagnosis")
                .type("PRIMARY")
                .build();

        response = DiagnosisResponse.builder()
                .id(1L)
                .description("Test Diagnosis")
                .build();
    }

    @Test
    @WithMockUser
    void add_ShouldReturnOk() throws Exception {
        when(diagnosisService.addDiagnosis(any(DiagnosisRequest.class))).thenReturn(response);

        mockMvc.perform(post("/diagnosis/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getById_ShouldReturnDiagnosis() throws Exception {
        when(diagnosisService.selectDiagnosisByIdWithGet(1L)).thenReturn(response);

        mockMvc.perform(get("/diagnosis/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturnList() throws Exception {
        when(diagnosisService.selectAllDiagnoses()).thenReturn(List.of(response));

        mockMvc.perform(get("/diagnosis/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void update_ShouldReturnUpdatedDiagnosis() throws Exception {
        when(diagnosisService.updateDiagnosis(eq(1L), any(DiagnosisRequest.class))).thenReturn(response);

        mockMvc.perform(put("/diagnosis/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/diagnosis/delete/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
