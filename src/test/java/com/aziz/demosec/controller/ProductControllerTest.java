package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.ProductType;
import com.aziz.demosec.dto.pharmacy.ProductRequestDTO;
import com.aziz.demosec.dto.pharmacy.ProductResponseDTO;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;
import com.aziz.demosec.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreatedProduct() throws Exception {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("Test Product").type("MEDICATION").unit("BOX").description("Desc").build();
        ProductResponseDTO response = ProductResponseDTO.builder()
                .id(1L).name("Test Product").type("MEDICATION").build();

        when(productService.create(any(ProductRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getById_ShouldReturnProduct() throws Exception {
        ProductResponseDTO response = ProductResponseDTO.builder().id(1L).name("Test Product").build();
        when(productService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getByType_ShouldReturnList() throws Exception {
        ProductResponseDTO response = ProductResponseDTO.builder().id(1L).type("MEDICATION").build();
        when(productService.getByType(ProductType.MEDICATION)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/products/type/medication"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("MEDICATION"));
    }

    @Test
    void update_ShouldReturnUpdatedProduct() throws Exception {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("Updated")
                .description("Updated Desc")
                .type("MEDICATION")
                .unit("BOX")
                .build();
        ProductResponseDTO response = ProductResponseDTO.builder().id(1L).name("Updated").build();

        when(productService.update(eq(1L), any(ProductRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }
}
