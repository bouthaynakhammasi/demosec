package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.EmergencyInterventionStatus;
import com.aziz.demosec.dto.emergency.EmergencyInterventionRequestDTO;
import com.aziz.demosec.dto.emergency.EmergencyInterventionResponseDTO;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EmergencyInterventionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IEmergencyService emergencyService;

    @InjectMocks
    private EmergencyInterventionController emergencyInterventionController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(emergencyInterventionController)
                .setValidator(validator)
                .build();
    }

    @Test
    public void testDispatchIntervention_Success() throws Exception {
        EmergencyInterventionRequestDTO requestDTO = EmergencyInterventionRequestDTO.builder()
                .emergencyAlertId(1L)
                .clinicId(2L)
                .ambulanceId(3L)
                .build();

        EmergencyInterventionResponseDTO responseDTO = new EmergencyInterventionResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setEmergencyAlertId(1L);
        responseDTO.setClinicId(2L);
        responseDTO.setAmbulanceId(3L);
        responseDTO.setStatus(EmergencyInterventionStatus.DISPATCHED);
        responseDTO.setDispatchedAt(LocalDateTime.now());

        when(emergencyService.dispatchIntervention(any(EmergencyInterventionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/interventions/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.emergencyAlertId", is(1)))
                .andExpect(jsonPath("$.status", is("DISPATCHED")));
    }

    @Test
    public void testDispatch_ValidationFails() throws Exception {
        // Validation Error: Negative ID
        EmergencyInterventionRequestDTO requestDTO = EmergencyInterventionRequestDTO.builder()
                .emergencyAlertId(-5L)
                .clinicId(2L)
                .ambulanceId(3L)
                .build();

        mockMvc.perform(post("/api/interventions/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAll() throws Exception {
        EmergencyInterventionResponseDTO r1 = new EmergencyInterventionResponseDTO(); r1.setId(10L);
        EmergencyInterventionResponseDTO r2 = new EmergencyInterventionResponseDTO(); r2.setId(11L);

        when(emergencyService.getAllInterventions()).thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get("/api/interventions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(10)))
                .andExpect(jsonPath("$[1].id", is(11)));
    }

    @Test
    public void testUpdateStatus() throws Exception {
        EmergencyInterventionResponseDTO responseDTO = new EmergencyInterventionResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setStatus(EmergencyInterventionStatus.ARRIVED);

        when(emergencyService.updateInterventionStatus(eq(10L), eq(EmergencyInterventionStatus.ARRIVED))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/interventions/10/status")
                .param("status", "ARRIVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ARRIVED")));
    }
}
