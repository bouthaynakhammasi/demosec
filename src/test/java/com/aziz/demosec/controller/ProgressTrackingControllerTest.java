package com.aziz.demosec.controller;

import com.aziz.demosec.dto.ProgressTrackingRequest;
import com.aziz.demosec.dto.ProgressTrackingResponse;
import com.aziz.demosec.service.IProgressTrackingService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProgressTrackingController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProgressTrackingControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProgressTrackingService progressService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProgressTrackingRequest request;
    private ProgressTrackingResponse response;

    @BeforeEach
    void setUp() {
        request = ProgressTrackingRequest.builder()
                .goalId(1L)
                .patientId(1L)
                .date(LocalDate.now())
                .value(new BigDecimal("95.0"))
                .notes("Test Notes")
                .build();

        response = ProgressTrackingResponse.builder()
                .id(1L)
                .date(LocalDate.now())
                .value(new BigDecimal("95.0"))
                .notes("Test Notes")
                .build();
    }

    @Test
    @WithMockUser
    void addTracking_ShouldReturnOk() throws Exception {
        when(progressService.addTracking(any(ProgressTrackingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/progress-tracking")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.value").value(95.0));
    }

    @Test
    @WithMockUser
    void getTrackingById_ShouldReturnTracking() throws Exception {
        when(progressService.getTrackingById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/progress-tracking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAllTrackings_ShouldReturnList() throws Exception {
        when(progressService.getAllTrackings()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/progress-tracking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void updateTracking_ShouldReturnUpdatedTracking() throws Exception {
        when(progressService.updateTracking(eq(1L), any(ProgressTrackingRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/progress-tracking/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void deleteTracking_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/progress-tracking/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
