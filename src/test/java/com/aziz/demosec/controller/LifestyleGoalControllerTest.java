package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LifestyleGoalRequest;
import com.aziz.demosec.dto.LifestyleGoalResponse;
import com.aziz.demosec.service.ILifestyleGoalService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LifestyleGoalController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for unit testing
class LifestyleGoalControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ILifestyleGoalService goalService;

    @Autowired
    private ObjectMapper objectMapper;

    private LifestyleGoalRequest request;
    private LifestyleGoalResponse response;

    @BeforeEach
    void setUp() {
        request = LifestyleGoalRequest.builder()
                .patientId(1L)
                .category("WEIGHT_LOSS")
                .targetValue(new BigDecimal("80.0"))
                .baselineValue(new BigDecimal("90.0"))
                .targetDate(LocalDate.now().plusMonths(2))
                .build();

        response = LifestyleGoalResponse.builder()
                .id(1L)
                .category("WEIGHT_LOSS")
                .targetValue(new BigDecimal("80.0"))
                .baselineValue(new BigDecimal("90.0"))
                .targetDate(LocalDate.now().plusMonths(2))
                .status("IN_PROGRESS")
                .build();
    }

    @Test
    @WithMockUser
    void addGoal_ShouldReturnOk() throws Exception {
        when(goalService.addGoal(any(LifestyleGoalRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/lifestyle-goals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.category").value("WEIGHT_LOSS"));
    }

    @Test
    @WithMockUser
    void getGoalById_ShouldReturnGoal() throws Exception {
        when(goalService.getGoalById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/lifestyle-goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAllGoals_ShouldReturnList() throws Exception {
        when(goalService.getAllGoals()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/lifestyle-goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void updateGoal_ShouldReturnUpdatedGoal() throws Exception {
        when(goalService.updateGoal(eq(1L), any(LifestyleGoalRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/lifestyle-goals/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void deleteGoal_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/lifestyle-goals/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
