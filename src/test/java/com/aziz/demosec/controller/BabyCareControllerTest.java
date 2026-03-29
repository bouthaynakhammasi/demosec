package com.aziz.demosec.controller;

import com.aziz.demosec.dto.baby.*;
import com.aziz.demosec.Entities.JournalEntryType;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;
import com.aziz.demosec.service.BabyCareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(BabyCareController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "PATIENT") // Simulate an authenticated patient user
class BabyCareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BabyCareService babyService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private BabyProfileRequestDTO requestDTO;
    private BabyProfileResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new BabyProfileRequestDTO();
        requestDTO.setName("Alex");
        requestDTO.setBirthDate(LocalDate.now().minusMonths(1));
        requestDTO.setGender("MALE");
        requestDTO.setBirthWeight(3.5);
        requestDTO.setBirthHeight(50.0);
        requestDTO.setPhotoUrl("http://example.com/photo.jpg");
        requestDTO.setPriorities(java.util.List.of("Nutrition"));

        responseDTO = new BabyProfileResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setName("Alex");
    }

    @Test
    @DisplayName("POST /api/baby-care/profile should create a profile")
    void createProfile_Endpoints_Success() throws Exception {
        // Arrange
        when(babyService.createProfile(anyLong(), any(BabyProfileRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/baby-care/profile")
                        .with(csrf())
                        .param("parentId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Alex"));
    }

    @Test
    @DisplayName("GET /api/baby-care/profile/{id} should return profile")
    void getProfileByPatientId_Endpoints_Success() throws Exception {
        // Arrange
        when(babyService.getProfileByPatientId(anyLong())).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/baby-care/profile/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Alex"));
    }

    @Test
    @DisplayName("GET /api/baby-care/profile/{id} should return 404 if profile not found")
    void getProfileByPatientId_Endpoints_NotFound() throws Exception {
        // Arrange
        when(babyService.getProfileByPatientId(anyLong())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/baby-care/profile/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/baby-care/journal/{babyId} should add journal entry")
    void addJournalEntry_Endpoints_Success() throws Exception {
        // Arrange
        JournalEntryRequestDTO dto = new JournalEntryRequestDTO();
        dto.setType(JournalEntryType.FEEDING);
        dto.setValue("120ml Milk");
        
        when(babyService.addJournalEntry(anyLong(), eq(JournalEntryType.FEEDING), anyString(), any(), any()))
                .thenReturn(new JournalEntryResponseDTO());

        // Act & Assert
        mockMvc.perform(post("/api/baby-care/journal/10")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/baby-care/dashboard/{babyId} should fetch dashboard")
    void getDashboard_Endpoints_Success() throws Exception {
        // Arrange
        BabyDashboardDTO dashboard = new BabyDashboardDTO();
        dashboard.setId(10L);
        dashboard.setName("Alex");
        dashboard.setAge("3 months");
        when(babyService.getDashboard(anyLong())).thenReturn(dashboard);

        // Act & Assert
        mockMvc.perform(get("/api/baby-care/dashboard/10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.age").value("3 months"));
    }

    @Test
    @DisplayName("DELETE /api/baby-care/journal/{id} should delete record")
    void deleteJournalEntry_Endpoints_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/baby-care/journal/100").with(csrf()))
                .andExpect(status().isOk());
    }
}
