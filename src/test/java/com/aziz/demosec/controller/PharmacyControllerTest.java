package com.aziz.demosec.controller;

import com.aziz.demosec.dto.pharmacy.PharmacyRequestDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyResponseDTO;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;
import com.aziz.demosec.service.IPharmacyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PharmacyController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PharmacyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPharmacyService pharmacyService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreatedPharmacy() throws Exception {
        // Arrange
        PharmacyRequestDTO request = PharmacyRequestDTO.builder()
                .name("Test Pharmacy")
                .address("Test Address")
                .build();
        PharmacyResponseDTO response = PharmacyResponseDTO.builder()
                .id(1L)
                .name("Test Pharmacy")
                .address("Test Address")
                .build();

        when(pharmacyService.create(any(PharmacyRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/pharmacy/pharmacies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Pharmacy"));
    }

    @Test
    void getById_ShouldReturnPharmacy() throws Exception {
        // Arrange
        PharmacyResponseDTO response = PharmacyResponseDTO.builder().id(1L).name("Test Pharmacy").build();
        when(pharmacyService.getById(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/pharmacy/pharmacies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Pharmacy"));
    }

    @Test
    void search_ShouldReturnList() throws Exception {
        // Arrange
        PharmacyResponseDTO response = PharmacyResponseDTO.builder().id(1L).name("Centrale").build();
        when(pharmacyService.search("centrale")).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/api/pharmacy/pharmacies/search")
                .param("name", "centrale"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Centrale"));
    }

    @Test
    void update_ShouldReturnUpdatedPharmacy() throws Exception {
        // Arrange
        PharmacyRequestDTO request = PharmacyRequestDTO.builder().name("Updated").address("Address").build();
        PharmacyResponseDTO response = PharmacyResponseDTO.builder().id(1L).name("Updated").build();
        when(pharmacyService.update(eq(1L), any(PharmacyRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/pharmacy/pharmacies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/pharmacy/pharmacies/1"))
                .andExpect(status().isNoContent());
    }
}
