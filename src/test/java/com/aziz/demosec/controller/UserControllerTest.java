package com.aziz.demosec.controller;

import com.aziz.demosec.domain.Role;
import com.aziz.demosec.dto.user.UserRequestDTO;
import com.aziz.demosec.dto.user.UserResponseDTO;
import com.aziz.demosec.service.IUserService;
import com.aziz.demosec.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private com.aziz.demosec.security.CustomUserDetailsService userDetailsService;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponseDTO userResponseDTO;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        userResponseDTO = UserResponseDTO.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .role(Role.VISITOR)
                .phone("12345678")
                .birthDate(LocalDate.of(1990, 1, 1))
                .enabled(true)
                .build();

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFullName("John Doe");
        userRequestDTO.setEmail("john@example.com");
        userRequestDTO.setPassword("password123");
        userRequestDTO.setRole(Role.VISITOR);
        userRequestDTO.setPhone("12345678");
        userRequestDTO.setBirthDate(LocalDate.of(1990, 1, 1));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void getProfile_ShouldReturnUserResponseDTO() throws Exception {
        when(userService.getByEmail("john@example.com")).thenReturn(userResponseDTO);

        mockMvc.perform(get("/user/profile")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void updateProfile_ShouldReturnUpdatedUserResponseDTO() throws Exception {
        when(userService.updateByEmail(anyString(), any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(put("/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @WithMockUser
    void create_ShouldReturnCreatedUser() throws Exception {
        when(userService.create(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getById_ShouldReturnUser() throws Exception {
        when(userService.getById(1L)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturnListOfUsers() throws Exception {
        when(userService.getAll()).thenReturn(Collections.singletonList(userResponseDTO));

        mockMvc.perform(get("/api/users").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void update_ShouldReturnUpdatedUser() throws Exception {
        when(userService.update(anyLong(), any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void toggleEnabled_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).toggleEnabled(1L);

        mockMvc.perform(patch("/api/users/1/toggle").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void getByRole_ShouldReturnUsers_WhenRoleIsValid() throws Exception {
        when(userService.getByRole(Role.VISITOR)).thenReturn(Collections.singletonList(userResponseDTO));

        mockMvc.perform(get("/api/users/role/VISITOR").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void getByRole_ShouldReturnBadRequest_WhenRoleIsInvalid() throws Exception {
        mockMvc.perform(get("/api/users/role/INVALID_ROLE").with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
