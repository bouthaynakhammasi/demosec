package com.aziz.demosec.controller;

import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.service.AuthService;
import com.aziz.demosec.service.FileStorageService;
import com.aziz.demosec.security.jwt.JwtService;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_ShouldReturnOk_WhenCredentialsAreValid() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
        AuthResponse authResponse = new AuthResponse("mockToken", "user@example.com", "User Name", "ROLE_PATIENT");

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void register_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        String userJson = "{\"fullName\":\"John Doe\",\"email\":\"john@example.com\",\"password\":\"password123\",\"role\":\"PATIENT\",\"phone\":\"12345678\",\"birthDate\":\"1990-01-01\"}";

        com.aziz.demosec.domain.User mockUser = new com.aziz.demosec.domain.User();
        mockUser.setEmail("john@example.com");

        when(authService.register(any(), any())).thenReturn(mockUser);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("user", userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User created: john@example.com"));
    }
}
