package com.aziz.demosec.controller;

import com.aziz.demosec.dto.emergency.AmbulanceRequestDTO;
import com.aziz.demosec.dto.emergency.AmbulanceResponseDTO;
import com.aziz.demosec.service.IEmergencyService;
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
public class AmbulanceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IEmergencyService emergencyService;

    @InjectMocks
    private AmbulanceController ambulanceController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(ambulanceController)
                .setValidator(validator)
                .build();
    }

    @Test
    public void testCreateAmbulance_Success() throws Exception {
        AmbulanceRequestDTO requestDTO = AmbulanceRequestDTO.builder()
                .clinicId(10L)
                .licensePlate("123-TU-456")
                .status("AVAILABLE")
                .build();

        AmbulanceResponseDTO responseDTO = new AmbulanceResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setClinicId(10L);
        responseDTO.setLicensePlate("123-TU-456");
        responseDTO.setStatus("AVAILABLE");

        when(emergencyService.createAmbulance(any(AmbulanceRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/ambulances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.licensePlate", is("123-TU-456")));
    }

    @Test
    public void testCreateAmbulance_ValidationFails() throws Exception {
        // Validation Error: plate too short string, status invalid
        AmbulanceRequestDTO requestDTO = AmbulanceRequestDTO.builder()
                .clinicId(10L)
                .licensePlate("1") 
                .status("NOT_A_VALID_STATUS")
                .build();

        mockMvc.perform(post("/api/ambulances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllAmbulances() throws Exception {
        AmbulanceResponseDTO a1 = new AmbulanceResponseDTO(); a1.setId(1L);
        AmbulanceResponseDTO a2 = new AmbulanceResponseDTO(); a2.setId(2L);

        when(emergencyService.getAllAmbulances()).thenReturn(Arrays.asList(a1, a2));

        mockMvc.perform(get("/api/ambulances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetByClinic() throws Exception {
        AmbulanceResponseDTO a1 = new AmbulanceResponseDTO(); a1.setId(5L); a1.setClinicId(10L);

        when(emergencyService.getAmbulancesByClinic(10L)).thenReturn(Arrays.asList(a1));

        mockMvc.perform(get("/api/ambulances/clinic/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clinicId", is(10)));
    }

    @Test
    public void testDeleteAmbulance() throws Exception {
        doNothing().when(emergencyService).deleteAmbulance(1L);

        mockMvc.perform(delete("/api/ambulances/1"))
                .andExpect(status().isNoContent());
    }
}
