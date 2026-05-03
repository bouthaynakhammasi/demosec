package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.EmergencyAlertStatus;
import com.aziz.demosec.Entities.EmergencySeverity;
import com.aziz.demosec.dto.emergency.EmergencyAlertRequestDTO;
import com.aziz.demosec.dto.emergency.EmergencyAlertResponseDTO;
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
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

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
public class EmergencyAlertControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IEmergencyService emergencyService;

    @InjectMocks
    private EmergencyAlertController emergencyAlertController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        // Enregistre manuellement le validateur Hibernate (JSR-380) pour standaloneSetup
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(emergencyAlertController)
                .setValidator(validator)
                .build();
    }

    @Test
    public void testCreateAlert_Success() throws Exception {
        EmergencyAlertRequestDTO requestDTO = EmergencyAlertRequestDTO.builder()
                .smartDeviceId(10L)
                .severity(EmergencySeverity.CRITICAL)
                .latitude(36.8065)
                .longitude(10.1815)
                .build();

        EmergencyAlertResponseDTO responseDTO = new EmergencyAlertResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setSmartDeviceId(10L);
        responseDTO.setStatus(EmergencyAlertStatus.PENDING);
        responseDTO.setSeverity(EmergencySeverity.CRITICAL);
        responseDTO.setLatitude(36.8065);
        responseDTO.setLongitude(10.1815);

        when(emergencyService.createAlert(any(EmergencyAlertRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/emergency-alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.smartDeviceId", is(10)))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.severity", is("CRITICAL")));
    }

    @Test
    public void testCreateAlert_ValidationFails() throws Exception {
        // Validation Error: smartDeviceId is Negative, latitude is out of bounds
        EmergencyAlertRequestDTO requestDTO = EmergencyAlertRequestDTO.builder()
                .smartDeviceId(-5L) 
                .severity(EmergencySeverity.HIGH)
                .latitude(100.0) // invalide: > 90
                .longitude(10.1815)
                .build();

        mockMvc.perform(post("/api/emergency-alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest()); 
    }

    @Test
    public void testGetAllAlerts() throws Exception {
        EmergencyAlertResponseDTO alert1 = new EmergencyAlertResponseDTO();
        alert1.setId(1L);
        EmergencyAlertResponseDTO alert2 = new EmergencyAlertResponseDTO();
        alert2.setId(2L);
        List<EmergencyAlertResponseDTO> list = Arrays.asList(alert1, alert2);

        when(emergencyService.getAllAlerts()).thenReturn(list);

        mockMvc.perform(get("/api/emergency-alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    public void testGetAlertById() throws Exception {
        EmergencyAlertResponseDTO responseDTO = new EmergencyAlertResponseDTO();
        responseDTO.setId(99L);
        responseDTO.setStatus(EmergencyAlertStatus.CLINIC_NOTIFIED);

        when(emergencyService.getAlertById(99L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/emergency-alerts/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(99)))
                .andExpect(jsonPath("$.status", is("CLINIC_NOTIFIED")));
    }

    @Test
    public void testUpdateStatus() throws Exception {
        EmergencyAlertResponseDTO responseDTO = new EmergencyAlertResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setStatus(EmergencyAlertStatus.RESOLVED);

        when(emergencyService.updateAlertStatus(eq(1L), eq(EmergencyAlertStatus.RESOLVED))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/emergency-alerts/1/status")
                .param("status", "RESOLVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RESOLVED")));
    }

    @Test
    public void testCancelAlert() throws Exception {
        EmergencyAlertResponseDTO responseDTO = new EmergencyAlertResponseDTO();
        responseDTO.setId(5L);
        responseDTO.setStatus(EmergencyAlertStatus.RESOLVED);
        responseDTO.setCanceledByPatient(true);

        when(emergencyService.cancelAlertByPatient(5L)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/emergency-alerts/5/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.status", is("RESOLVED")))
                .andExpect(jsonPath("$.canceledByPatient", is(true)));
    }
}
