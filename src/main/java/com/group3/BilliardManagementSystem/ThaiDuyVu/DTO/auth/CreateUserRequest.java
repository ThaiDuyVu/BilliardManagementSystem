package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth;

import com.group3.BilliardManagementSystem.Entity.User;
import lombok.Data;

import java.util.Set;

@Data
public class CreateUserRequest {

    private String username;

    private String email;

    private String password;

    private String fullName;

    private User.UserStatus status;

    private Set<Long> roleIds;
}