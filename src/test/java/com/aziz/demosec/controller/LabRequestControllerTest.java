package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.LabRequestStatus;
import com.aziz.demosec.dto.LabRequestRequest;
import com.aziz.demosec.dto.LabRequestResponse;
import com.aziz.demosec.dto.RequestedBy;
import com.aziz.demosec.service.LabRequestService;
import com.aziz.demosec.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(LabRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class LabRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LabRequestService labRequestService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private com.aziz.demosec.security.CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private LabRequestResponse responseDto;
    private LabRequestRequest requestDto;

    @BeforeEach
    void setUp() {
        responseDto = LabRequestResponse.builder()
                .id(1L)
                .testType("Blood Test")
                .status(LabRequestStatus.PENDING)
                .build();

        requestDto = new LabRequestRequest();
        requestDto.setPatientId(1L);
        requestDto.setLaboratoryId(1L);
        requestDto.setTestType("Blood Test");
        requestDto.setRequestedBy(RequestedBy.PATIENT);
    }

    @Test
    void create_ShouldReturnCreatedLabRequest() throws Exception {
        when(labRequestService.create(any(LabRequestRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/lab-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getById_ShouldReturnLabRequest() throws Exception {
        when(labRequestService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/lab-requests/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAll_ShouldReturnListOfLabRequests() throws Exception {
        when(labRequestService.getAll()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/lab-requests").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void update_ShouldReturnUpdatedLabRequest() throws Exception {
        when(labRequestService.update(anyLong(), any(LabRequestRequest.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/lab-requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(labRequestService).delete(1L);

        mockMvc.perform(delete("/api/lab-requests/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getByPatient_ShouldReturnLabRequests() throws Exception {
        when(labRequestService.getByPatient(1L)).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/lab-requests/patient/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
