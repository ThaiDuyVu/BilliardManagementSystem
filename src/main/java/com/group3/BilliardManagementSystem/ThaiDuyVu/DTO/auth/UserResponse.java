package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private String fullName;

    private String status;

    private Set<String> roleNames;
}