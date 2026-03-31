package com.aziz.demosec.service;

import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.user.ChangePasswordDTO;
import com.aziz.demosec.dto.user.UserRequestDTO;
import com.aziz.demosec.dto.user.UserResponseDTO;
import com.aziz.demosec.repository.PharmacistRepository;
import com.aziz.demosec.repository.UserRepository;
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
    private final PharmacistRepository pharmacistRepository;

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
                .birthDate(dto.getBirthDate())
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

        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
        }

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
           user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setRole(dto.getRole());
        user.setPhone(dto.getPhone());
        user.setBirthDate(dto.getBirthDate());

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

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
        return toDTO(user);
    }

    @Override
    public UserResponseDTO updateByEmail(String email, UserRequestDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setBirthDate(dto.getBirthDate());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return toDTO(userRepository.save(user));
    }

    @Override
    public void changePassword(Long id, ChangePasswordDTO dto) {
        User user = findOrThrow(id);

        // Verify the current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    /**
     * Converts User to UserResponseDTO.
     * If the user is a PHARMACIST, also includes pharmacyId and pharmacyName.
     */
    private UserResponseDTO toDTO(User user) {
        UserResponseDTO.UserResponseDTOBuilder builder = UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .enabled(user.isEnabled());

        // If user is a Pharmacist, fetch and include pharmacyId
        if (user.getRole() == Role.PHARMACIST) {
            pharmacistRepository.findById(user.getId()).ifPresent(pharmacist -> {
                if (pharmacist.getPharmacy() != null) {
                    builder.pharmacyId(pharmacist.getPharmacy().getId());
                    builder.pharmacyName(pharmacist.getPharmacy().getName());
                }
            });
        }

        return builder.build();
    }
}