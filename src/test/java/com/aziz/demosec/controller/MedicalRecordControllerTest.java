package com.aziz.demosec.controller;

import com.aziz.demosec.dto.MedicalRecordRequest;
import com.aziz.demosec.dto.MedicalRecordResponse;
import com.aziz.demosec.service.IMedicalRecordService;
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

@WebMvcTest(MedicalRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
class MedicalRecordControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IMedicalRecordService medicalRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    private MedicalRecordRequest request;
    private MedicalRecordResponse response;

    @BeforeEach
    void setUp() {
        request = MedicalRecordRequest.builder()
                .patientId(1L)
                .build();

        response = MedicalRecordResponse.builder()
                .id(1L)
                .patientId(1L)
                .build();
    }

    @Test
    @WithMockUser
    void addMedicalRecord_ShouldReturnResponse() throws Exception {
        when(medicalRecordService.addMedicalRecord(any(MedicalRecordRequest.class))).thenReturn(response);

        mockMvc.perform(post("/medical-record/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getById_ShouldReturnResponse() throws Exception {
        when(medicalRecordService.selectMedicalRecordByIdWithGet(1L)).thenReturn(response);

        mockMvc.perform(get("/medical-record/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturnList() throws Exception {
        when(medicalRecordService.selectAllMedicalRecords()).thenReturn(List.of(response));

        mockMvc.perform(get("/medical-record/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void deleteById_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/medical-record/delete/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getByPatientId_ShouldReturnResponse() throws Exception {
        when(medicalRecordService.selectMedicalRecordByPatientId(1L)).thenReturn(response);

        mockMvc.perform(get("/medical-record/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
