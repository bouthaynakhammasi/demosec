package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Doctor;
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
public class UserServiceImpl implements UserService {

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

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getEmail() != null) {
            if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
            }
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
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

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .filter(User::isEnabled)
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
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getBirthDate() != null) user.setBirthDate(dto.getBirthDate());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return toDTO(userRepository.save(user));
    }

    @Override
    public void changePassword(Long id, ChangePasswordDTO dto) {
        User user = findOrThrow(id);

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    private UserResponseDTO toDTO(User user) {
        String specialty = null;
        if (user instanceof Doctor) {
            specialty = ((Doctor) user).getSpecialty();
        }

        UserResponseDTO.UserResponseDTOBuilder builder = UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .photo(user.getPhoto())
                .enabled(user.isEnabled())
                .specialty(specialty);

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
