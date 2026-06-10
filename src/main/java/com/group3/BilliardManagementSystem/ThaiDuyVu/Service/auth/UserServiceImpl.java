package com.group3.BilliardManagementSystem.ThaiDuyVu.Service.auth;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.CreateUserRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.UpdateUserRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.UserResponse;
import com.group3.BilliardManagementSystem.Entity.Role;
import com.group3.BilliardManagementSystem.Entity.User;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.RoleRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.isDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isDeleted()) {
            throw new RuntimeException("User not found");
        }

        return mapToResponse(user);
    }
    @Override
    public List<UserResponse> getUsersWithoutEmployee() {

        return userRepository.findByEmployeeIsNull()
                .stream()
                .filter(user -> !user.isDeleted())
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Set<Role> roles = new HashSet<>(
                roleRepository.findAllById(request.getRoleIds())
        );

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .status(
                        request.getStatus() != null
                                ? request.getStatus()
                                : User.UserStatus.ACTIVE
                )
                .roles(roles)
                .deleted(false)
                .build();

        return mapToResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isDeleted()) {
            throw new RuntimeException("User not found");
        }
        User existedUser = userRepository
                .findByUsername(request.getUsername())
                .orElse(null);

        if (existedUser != null
                && !existedUser.getId().equals(id)) {

            throw new RuntimeException("Username already exists");
        }
        Set<Role> roles = new HashSet<>(
                roleRepository.findAllById(request.getRoleIds())
        );

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setStatus(request.getStatus());
        user.setRoles(roles);

        return mapToResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setDeleted(true);

        userRepository.save(user);
    }

    private UserResponse mapToResponse(User user) {

        Set<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .status(user.getStatus().name())
                .roleNames(roleNames)
                .build();
    }
}