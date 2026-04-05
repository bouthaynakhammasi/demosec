package com.aziz.demosec.controller;

import com.aziz.demosec.dto.ConsultationRequest;
import com.aziz.demosec.dto.ConsultationResponse;
import com.aziz.demosec.service.IConsultationService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConsultationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConsultationControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IConsultationService consultationService;

    @Autowired
    private ObjectMapper objectMapper;

    private ConsultationRequest request;
    private ConsultationResponse response;

    @BeforeEach
    void setUp() {
        request = ConsultationRequest.builder()
                .medicalRecordId(1L)
                .doctorId(2L)
                .date(LocalDateTime.now())
                .observations("Typical symptoms")
                .notes("Test Notes")
                .build();

        response = ConsultationResponse.builder()
                .id(1L)
                .observations("Typical symptoms")
                .build();
    }

    @Test
    @WithMockUser
    void add_ShouldReturnOk() throws Exception {
        when(consultationService.addConsultation(any(ConsultationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/consultation/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getById_ShouldReturnConsultation() throws Exception {
        when(consultationService.selectConsultationByIdWithGet(1L)).thenReturn(response);

        mockMvc.perform(get("/consultation/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturnList() throws Exception {
        when(consultationService.selectAllConsultations()).thenReturn(List.of(response));

        mockMvc.perform(get("/consultation/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void update_ShouldReturnUpdatedConsultation() throws Exception {
        when(consultationService.updateConsultation(eq(1L), any(ConsultationRequest.class))).thenReturn(response);

        mockMvc.perform(put("/consultation/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/consultation/delete/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
