package com.aziz.demosec.service;

import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.user.UserRequestDTO;
import com.aziz.demosec.dto.user.UserResponseDTO;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.Entities.Doctor;
import com.aziz.demosec.service.IUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO create(UserRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
        }

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .phone(dto.getPhone())
                .birthDate(dto.getBirthDate()) // ✅ CORRECT
                .enabled(true)
                .build();

        return toDTO(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = findOrThrow(id);

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getEmail() != null) {
            if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
            }
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());


        if (dto.getBirthDate() != null) user.setBirthDate(dto.getBirthDate());


        return toDTO(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        userRepository.deleteById(id);
    }

    @Override
    public void toggleEnabled(Long id) {
        User user = findOrThrow(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    // Conversion User -> UserResponseDTO
    private UserResponseDTO toDTO(User user) {
        String specialty = null;
        if (user instanceof Doctor) {
            specialty = ((Doctor) user).getSpecialty();
        }

        // Si birthDate est LocalDate
        java.time.LocalDate birthDate = user.getBirthDate(); // peut être null

        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .birthDate(birthDate)
                .enabled(user.isEnabled())
                .specialty(specialty)
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getByRole(Role role) {
        List<User> users = userRepository.findByRole(role);
        System.out.println("[AUDIT] Doctors found with role " + role + ": " + users.size());


        return users.stream()
                .filter(User::isEnabled)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
