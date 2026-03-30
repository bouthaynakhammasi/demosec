package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LabResultRequest;
import com.aziz.demosec.dto.LabResultResponse;
import com.aziz.demosec.service.LabResultService;
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

@WebMvcTest(LabResultController.class)
@AutoConfigureMockMvc(addFilters = false)
class LabResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LabResultService labResultService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private com.aziz.demosec.security.CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private LabResultResponse responseDto;
    private LabResultRequest requestDto;

    @BeforeEach
    void setUp() {
        responseDto = LabResultResponse.builder()
                .id(1L)
                .status("COMPLETED")
                .isAbnormal(false)
                .build();

        requestDto = new LabResultRequest();
        requestDto.setLabRequestId(1L);
        requestDto.setStatus("COMPLETED");
        requestDto.setIsAbnormal(false);
        requestDto.setResultData("Normal values");
        requestDto.setTechnicianName("John Admin");
    }

    @Test
    void create_ShouldReturnCreatedLabResult() throws Exception {
        when(labResultService.create(any(LabResultRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/lab-results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAll_ShouldReturnListOfLabResults() throws Exception {
        when(labResultService.getAll()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/lab-results").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getById_ShouldReturnLabResult() throws Exception {
        when(labResultService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/lab-results/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void update_ShouldReturnUpdatedLabResult() throws Exception {
        when(labResultService.update(anyLong(), any(LabResultRequest.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/lab-results/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(labResultService).delete(1L);

        mockMvc.perform(delete("/api/lab-results/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAbnormalResults_ShouldReturnListOfLabResults() throws Exception {
        when(labResultService.getAbnormalResults()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/lab-results/abnormal").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
