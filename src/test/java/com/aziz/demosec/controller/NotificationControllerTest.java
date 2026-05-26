package com.aziz.demosec.controller;

import com.aziz.demosec.dto.pharmacy.NotificationResponseDTO;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;
import com.aziz.demosec.service.INotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private INotificationService notificationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUnread_ShouldReturnList() throws Exception {
        NotificationResponseDTO dto = NotificationResponseDTO.builder().id(1L).title("Unread").build();
        when(notificationService.getUnread(100L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pharmacy/notifications/user/100/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].title").value("Unread"));
    }

    @Test
    void countUnread_ShouldReturnCountMap() throws Exception {
        when(notificationService.countUnread(100L)).thenReturn(5L);

        mockMvc.perform(get("/api/pharmacy/notifications/user/100/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(5));
    }

    @Test
    void markAsRead_ShouldReturnStatusOk() throws Exception {
        NotificationResponseDTO dto = NotificationResponseDTO.builder().id(1L).isRead(true).build();
        when(notificationService.markAsRead(1L)).thenReturn(dto);

        mockMvc.perform(patch("/api/pharmacy/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }
}
