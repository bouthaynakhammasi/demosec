package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LifestylePlanRequest;
import com.aziz.demosec.dto.LifestylePlanResponse;
import com.aziz.demosec.service.ILifestylePlanService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LifestylePlanController.class)
@AutoConfigureMockMvc(addFilters = false)
class LifestylePlanControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ILifestylePlanService planService;

    @Autowired
    private ObjectMapper objectMapper;

    private LifestylePlanRequest request;
    private LifestylePlanResponse response;

    @BeforeEach
    void setUp() {
        request = LifestylePlanRequest.builder()
                .goalId(1L)
                .nutritionistId(2L)
                .title("Test Plan")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .status("ACTIVE")
                .build();

        response = LifestylePlanResponse.builder()
                .id(1L)
                .title("Test Plan")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .status("ACTIVE")
                .build();
    }

    @Test
    @WithMockUser
    void addPlan_ShouldReturnOk() throws Exception {
        when(planService.addPlan(any(LifestylePlanRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/lifestyle-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Plan"));
    }

    @Test
    @WithMockUser
    void getPlanById_ShouldReturnPlan() throws Exception {
        when(planService.getPlanById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/lifestyle-plans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAllPlans_ShouldReturnList() throws Exception {
        when(planService.getAllPlans()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/lifestyle-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void updatePlan_ShouldReturnUpdatedPlan() throws Exception {
        when(planService.updatePlan(eq(1L), any(LifestylePlanRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/lifestyle-plans/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void deletePlan_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/lifestyle-plans/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void countAll_ShouldReturnCount() throws Exception {
        when(planService.countAll()).thenReturn(10L);

        mockMvc.perform(get("/api/lifestyle-plans/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }
}
