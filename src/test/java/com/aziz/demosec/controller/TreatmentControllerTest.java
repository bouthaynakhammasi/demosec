package com.aziz.demosec.controller;

import com.aziz.demosec.dto.TreatmentRequest;
import com.aziz.demosec.dto.TreatmentResponse;
import com.aziz.demosec.service.ITreatmentService;
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

@WebMvcTest(TreatmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class TreatmentControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITreatmentService treatmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private TreatmentRequest request;
    private TreatmentResponse response;

    @BeforeEach
    void setUp() {
        request = TreatmentRequest.builder()
                .consultationId(1L)
                .treatmentType("MEDICATION")
                .description("Test Treatment")
                .startDate("2026-03-28")
                .endDate("2026-04-28")
                .status("ONGOING")
                .build();

        response = TreatmentResponse.builder()
                .id(1L)
                .description("Test Treatment")
                .build();
    }

    @Test
    @WithMockUser
    void add_ShouldReturnOk() throws Exception {
        when(treatmentService.addTreatment(any(TreatmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/treatment/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getById_ShouldReturnTreatment() throws Exception {
        when(treatmentService.selectTreatmentByIdWithGet(1L)).thenReturn(response);

        mockMvc.perform(get("/treatment/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturnList() throws Exception {
        when(treatmentService.selectAllTreatments()).thenReturn(List.of(response));

        mockMvc.perform(get("/treatment/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void update_ShouldReturnUpdatedTreatment() throws Exception {
        when(treatmentService.updateTreatment(eq(1L), any(TreatmentRequest.class))).thenReturn(response);

        mockMvc.perform(put("/treatment/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/treatment/delete/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
