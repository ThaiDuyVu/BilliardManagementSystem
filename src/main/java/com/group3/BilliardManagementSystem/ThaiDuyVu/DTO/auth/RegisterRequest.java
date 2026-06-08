package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth;


import lombok.Data;

@Data
public class RegisterRequest {

    private String username;
    private String email;
    private String password;
    private String fullName;
}