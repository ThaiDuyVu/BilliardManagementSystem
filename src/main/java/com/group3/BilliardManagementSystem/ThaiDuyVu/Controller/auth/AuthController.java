package com.group3.BilliardManagementSystem.ThaiDuyVu.Controller.auth;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.LoginRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.LoginResponse;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.RegisterRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}