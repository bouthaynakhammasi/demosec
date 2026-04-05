package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.AidRequestStatus;
import com.aziz.demosec.dto.donation.AidRequestDTO;
import com.aziz.demosec.dto.donation.AidRequestResponseDTO;
import com.aziz.demosec.service.IDonationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AidRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IDonationService donationService;

    @InjectMocks
    private AidRequestController aidRequestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(aidRequestController)
                .setValidator(validator)
                .build();
    }

    @Test
    public void testCreateAidRequest_Success() throws Exception {
        AidRequestDTO requestDTO = AidRequestDTO.builder()
                .patientId(15L)
                .description("Need financial help for MRI scan") // Valide: >10 caractères
                .supportingDocument("mri_presc.pdf")
                .build();

        AidRequestResponseDTO responseDTO = new AidRequestResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setPatientId(15L);
        responseDTO.setDescription("Need financial help for MRI scan");
        responseDTO.setStatus(AidRequestStatus.PENDING);

        when(donationService.createAidRequest(any(AidRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/aid-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.patientId", is(15)))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.description", is("Need financial help for MRI scan")));
    }

    @Test
    public void testCreateAidRequest_ValidationFails() throws Exception {
        // Validation Error: description is too short (< 10 caractères)
        AidRequestDTO requestDTO = AidRequestDTO.builder()
                .patientId(15L)
                .description("Short") 
                .build();

        mockMvc.perform(post("/api/aid-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest()); // Doit retourner une erreur 400
    }

    @Test
    public void testGetAllAidRequests() throws Exception {
        AidRequestResponseDTO r1 = new AidRequestResponseDTO(); r1.setId(1L);
        AidRequestResponseDTO r2 = new AidRequestResponseDTO(); r2.setId(2L);
        List<AidRequestResponseDTO> list = Arrays.asList(r1, r2);

        when(donationService.getAllAidRequests()).thenReturn(list);

        mockMvc.perform(get("/api/aid-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    public void testUpdateStatus() throws Exception {
        AidRequestResponseDTO responseDTO = new AidRequestResponseDTO();
        responseDTO.setId(5L);
        responseDTO.setStatus(AidRequestStatus.APPROVED);

        when(donationService.updateAidRequestStatus(eq(5L), eq(AidRequestStatus.APPROVED))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/aid-requests/5/status")
                .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    public void testDeleteAidRequest() throws Exception {
        doNothing().when(donationService).deleteAidRequest(1L);

        mockMvc.perform(delete("/api/aid-requests/1"))
                .andExpect(status().isNoContent());
    }
}
