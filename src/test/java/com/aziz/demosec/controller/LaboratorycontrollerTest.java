package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LaboratoryRequest;
import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.service.ILaboratoryService;
import com.aziz.demosec.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LaboratoryController.class)
@AutoConfigureMockMvc
class LaboratoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ILaboratoryService laboratoryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private com.aziz.demosec.security.CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private LaboratoryResponse responseDto;
    private LaboratoryRequest requestDto;

    @BeforeEach
    void setUp() {
        responseDto = new LaboratoryResponse();
        responseDto.setId(1L);
        responseDto.setName("Central Lab");

        requestDto = LaboratoryRequest.builder()
                .name("Central Lab")
                .address("123 Lab St")
                .phone("12345678")
                .openingHours("9-5")
                .email("lab@example.com")
                .build();
    }

    @Test
    @WithMockUser
    void create_ShouldReturnCreatedLaboratory() throws Exception {
        when(laboratoryService.create(any(LaboratoryRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/laboratories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getById_ShouldReturnLaboratory() throws Exception {
        when(laboratoryService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/laboratories/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturnListOfLaboratories() throws Exception {
        when(laboratoryService.getAll()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/laboratories").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(laboratoryService).delete(1L);

        mockMvc.perform(delete("/api/laboratories/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
