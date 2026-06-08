package com.group3.BilliardManagementSystem.ThaiDuyVu.Service.auth;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.LoginRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.LoginResponse;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse register(RegisterRequest request);
}