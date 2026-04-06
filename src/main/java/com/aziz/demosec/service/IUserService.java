package com.aziz.demosec.service;

import com.aziz.demosec.domain.Role;
import com.aziz.demosec.dto.user.UserRequestDTO;
import com.aziz.demosec.dto.user.UserResponseDTO;

import java.util.List;

public interface IUserService {
    UserResponseDTO create(UserRequestDTO dto);
    UserResponseDTO getById(Long id);
    List<UserResponseDTO> getAll();
    UserResponseDTO update(Long id, UserRequestDTO dto);
    void delete(Long id);
    void toggleEnabled(Long id);
    List<UserResponseDTO> getByRole(Role role);
    UserResponseDTO getByEmail(String email);
    UserResponseDTO updateByEmail(String email, UserRequestDTO dto);
}
