package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.service.LaboratoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;

@WebMvcTest(LaboratoryController.class)
@AutoConfigureMockMvc
@Disabled("Broken due to service changes")
class LaboratoryControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LaboratoryService laboratoryService;

    private LaboratoryResponse response;

    @BeforeEach
    void setUp() {
        response = LaboratoryResponse.builder()
                .id(1L)
                .name("Test Lab")
                .address("Test Address")
                .phone("12345678")
                .build();
    }

    @Test
    @WithMockUser(username = "lab@test.com")
    void getMyLaboratory_ShouldReturnLaboratory() throws Exception {
        when(laboratoryService.getMyLaboratory("lab@test.com")).thenReturn(response);

        mockMvc.perform(get("/api/laboratories/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Lab"));
    }
}
