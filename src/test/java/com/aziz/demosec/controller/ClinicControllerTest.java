package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.Clinic;
import com.aziz.demosec.dto.ClinicProfileResponse;
import com.aziz.demosec.dto.ClinicProfileUpdateRequest;
import com.aziz.demosec.repository.ClinicRepository;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClinicController.class)
@AutoConfigureMockMvc
class ClinicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClinicRepository clinicRepository;

    @MockBean
    private com.aziz.demosec.security.jwt.JwtService jwtService; // Required for Security context in WebMvcTest

    @MockBean
    private com.aziz.demosec.security.CustomUserDetailsService userDetailsService;

    private Clinic mockClinic;

    @BeforeEach
    void setUp() {
        mockClinic = new Clinic();
        mockClinic.setId(1L);
        mockClinic.setEmail("clinic@test.com");
        mockClinic.setPhone("12345678");
        mockClinic.setBirthDate(LocalDate.of(1990, 1, 1));
        mockClinic.setName("Test Clinic Name");
        mockClinic.setAddress("Test Address");
        mockClinic.setFullName("Test Clinic");
    }

    @Test
    @WithMockUser(username = "clinic@test.com")
    void getMe_ShouldReturnProfile() throws Exception {
        when(clinicRepository.findByEmail("clinic@test.com")).thenReturn(Optional.of(mockClinic));

        mockMvc.perform(get("/api/clinics/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Test Clinic"))
                .andExpect(jsonPath("$.email").value("clinic@test.com"));
    }

    @Test
    @WithMockUser(username = "clinic@test.com")
    void getMe_WhenNotFound_ShouldReturn404() throws Exception {
        when(clinicRepository.findByEmail("clinic@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clinics/me"))
                .andExpect(status().isNotFound());
    }
}
