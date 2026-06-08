package com.group3.BilliardManagementSystem.ThaiDuyVu.Service.auth;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.LoginRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.LoginResponse;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.RegisterRequest;
import com.group3.BilliardManagementSystem.Entity.Role;
import com.group3.BilliardManagementSystem.Entity.User;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.RoleRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Util.JwtUtil;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        if (user.isDeleted()) {
            throw new RuntimeException("User deleted");
        }

        Set<String> roles = user.getRoles() == null
                ? Set.of()
                : user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                roles
        );

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .roles(roles)
                .token(token)
                .build();
    }
    @Override
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setStatus(User.UserStatus.ACTIVE);

        Role defaultRole = roleRepository.findByName("ROLE_STAFF")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        user.setRoles(Set.of(defaultRole));

        userRepository.save(user);

        Set<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String jwtToken = jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                roleNames
        );

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .token(jwtToken)
                .roles(roleNames)
                .build();
    }
}