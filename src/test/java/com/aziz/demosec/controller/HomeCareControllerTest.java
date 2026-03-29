package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.HomeCareService;
import com.aziz.demosec.dto.homecare.*;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;
import com.aziz.demosec.service.HomeCareManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeCareController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class HomeCareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeCareManagementService homeCareService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllServices_ShouldReturnList() throws Exception {
        HomeCareService service = new HomeCareService();
        service.setId(1L);
        service.setName("Nursing");

        when(homeCareService.getAllActiveServices()).thenReturn(List.of(service));

        mockMvc.perform(get("/api/homecare/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Nursing"));
    }

    @Test
    void getProvidersByService_ShouldReturnProfiles() throws Exception {
        ProviderProfileDTO profile = ProviderProfileDTO.builder().id(10L).fullName("Dr. Smith").build();
        when(homeCareService.searchProviders(1L, 4.0)).thenReturn(List.of(profile));

        mockMvc.perform(get("/api/homecare/services/1/providers")
                .param("minRating", "4.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Dr. Smith"));
    }

    @Test
    void getProviderProfile_ShouldReturnProfile() throws Exception {
        ProviderProfileDTO profile = ProviderProfileDTO.builder().id(10L).fullName("Dr. Smith").build();
        when(homeCareService.getProviderProfile(10L)).thenReturn(profile);

        mockMvc.perform(get("/api/homecare/providers/10/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Dr. Smith"));
    }

    @Test
    void createRequest_ShouldReturn400IfAddressTooLong() throws Exception {
        CreateServiceRequestDTO dto = new CreateServiceRequestDTO();
        dto.setServiceId(1L);
        dto.setRequestedDateTime(LocalDateTime.now().plusDays(1));
        dto.setAddress("a".repeat(501)); // Max 500
        dto.setPatientNotes("Notes");

        mockMvc.perform(post("/api/homecare/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitReview_ShouldReturn400IfCommentTooLong() throws Exception {
        SubmitReviewDTO dto = new SubmitReviewDTO();
        dto.setRating(5);
        dto.setComment("c".repeat(1001)); // Max 1000

        mockMvc.perform(post("/api/homecare/requests/1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void completeRequest_ShouldReturn400IfProviderNotesTooLong() throws Exception {
        CompleteRequestDTO dto = new CompleteRequestDTO();
        dto.setProviderNotes("n".repeat(1001)); // Max 1000

        mockMvc.perform(put("/api/homecare/provider/requests/1/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
