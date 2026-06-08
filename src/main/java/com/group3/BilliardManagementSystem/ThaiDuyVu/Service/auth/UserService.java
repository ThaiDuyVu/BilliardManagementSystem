package com.group3.BilliardManagementSystem.ThaiDuyVu.Service.auth;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.CreateUserRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.UpdateUserRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    List<UserResponse> getUsersWithoutEmployee();

    UserResponse getUserById(Long id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}