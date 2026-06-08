package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}