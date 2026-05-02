package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LabTestRequest;
import com.aziz.demosec.dto.LabTestResponse;
import com.aziz.demosec.service.LabTestService;
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

@WebMvcTest(LabTestController.class)
@AutoConfigureMockMvc(addFilters = false)
class LabTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LabTestService labTestService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private com.aziz.demosec.security.CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private LabTestResponse responseDto;
    private LabTestRequest requestDto;

    @BeforeEach
    void setUp() {
        responseDto = LabTestResponse.builder()
                .id(1L)
                .name("Blood Test")
                .laboratoryId(1L)
                .build();

        requestDto = new LabTestRequest();
        requestDto.setName("Blood Test");
        requestDto.setLaboratoryId(1L);
        requestDto.setPrice(new java.math.BigDecimal("50.00"));
        requestDto.setTestType(com.aziz.demosec.Entities.TestType.BLOOD_TEST);
        requestDto.setCategory("General");
    }

    @Test
    void create_ShouldReturnCreatedLabTest() throws Exception {
        when(labTestService.create(any(LabTestRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/lab-tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getById_ShouldReturnLabTest() throws Exception {
        when(labTestService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/lab-tests/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAll_ShouldReturnListOfLabTests() throws Exception {
        when(labTestService.getAll()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/lab-tests").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getByLaboratory_ShouldReturnListOfLabTests() throws Exception {
        when(labTestService.getByLaboratory(1L)).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/lab-tests/laboratory/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void update_ShouldReturnUpdatedLabTest() throws Exception {
        when(labTestService.update(anyLong(), any(LabTestRequest.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/lab-tests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(labTestService).delete(1L);

        mockMvc.perform(delete("/api/lab-tests/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
