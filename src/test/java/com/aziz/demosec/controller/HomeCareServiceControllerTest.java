package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.HomeCareService;
import com.aziz.demosec.repository.HomeCareServiceRepository;
import com.aziz.demosec.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeCareServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class HomeCareServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeCareServiceRepository homeCareServiceRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private com.aziz.demosec.security.CustomUserDetailsService userDetailsService;

    @Test
    void getAllServices_ShouldReturnServices() throws Exception {
        HomeCareService service = new HomeCareService();
        service.setId(1L);
        service.setName("Nursing Care");
        service.setDescription("Professional nursing at home");
        service.setPrice(new BigDecimal("100.00"));

        when(homeCareServiceRepository.findAll()).thenReturn(Collections.singletonList(service));

        mockMvc.perform(get("/api/home-care-services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Nursing Care"))
                .andExpect(jsonPath("$[0].price").value(100.00));
    }
}
