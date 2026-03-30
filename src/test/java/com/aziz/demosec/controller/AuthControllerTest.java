package com.aziz.demosec.controller;

import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.service.IAuthService;
import com.aziz.demosec.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc // Enable security filters for real-world testing
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAuthService authService;

    @MockitoBean
    private JwtService jwtService; // Required because of security configuration

    @MockitoBean
    private com.aziz.demosec.security.CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser // Auth endpoints are public but it's good practice for consistency
    void register_ShouldReturnOk_WhenSuccessful() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "John Doe", "john@example.com", "password123", com.aziz.demosec.domain.Role.VISITOR, "12345678",
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
        );
        User user = new User();
        user.setEmail("john@example.com");

        when(authService.register(any(RegisterRequest.class))).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("User created: john@example.com"));
    }

    @Test
    @WithMockUser
    void register_ShouldReturnBadRequest_WhenExceptionThrown() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "John Doe", "john@example.com", "password123", com.aziz.demosec.domain.Role.VISITOR, "12345678",
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
        );

        when(authService.register(any(RegisterRequest.class))).thenThrow(new IllegalArgumentException("Email already used"));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already used"));
    }
}
