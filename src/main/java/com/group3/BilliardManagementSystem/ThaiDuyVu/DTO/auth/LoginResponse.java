package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class LoginResponse {
    private Long userId;
    private String username;
    private String token;
    private Set<String> roles;
}