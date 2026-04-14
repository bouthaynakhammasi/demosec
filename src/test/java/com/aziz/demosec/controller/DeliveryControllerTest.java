package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.Delivery;
import com.aziz.demosec.Entities.DeliveryStatus;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;
import com.aziz.demosec.service.DeliveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService deliveryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getByOrderId_ShouldReturnDelivery() throws Exception {
        Delivery delivery = Delivery.builder().id(10L).trackingNumber("TRK-1").build();
        when(deliveryService.getDeliveryByOrderId(1L)).thenReturn(Optional.of(delivery));

        mockMvc.perform(get("/api/pharmacy/deliveries/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value("TRK-1"));
    }

    @Test
    void updateStatus_ShouldReturnUpdatedDelivery() throws Exception {
        Delivery delivery = Delivery.builder().id(10L).status(DeliveryStatus.DELIVERED).build();
        when(deliveryService.updateDeliveryStatus(10L, DeliveryStatus.DELIVERED)).thenReturn(delivery);

        mockMvc.perform(patch("/api/pharmacy/deliveries/10/status")
                .param("status", "DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    void getByTrackingNumber_ShouldReturnDelivery() throws Exception {
        Delivery delivery = Delivery.builder().id(10L).trackingNumber("TRK-123").build();
        when(deliveryService.getDeliveryByTrackingNumber("TRK-123")).thenReturn(delivery);

        mockMvc.perform(get("/api/pharmacy/deliveries/tracking/TRK-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value("TRK-123"));
    }
}
