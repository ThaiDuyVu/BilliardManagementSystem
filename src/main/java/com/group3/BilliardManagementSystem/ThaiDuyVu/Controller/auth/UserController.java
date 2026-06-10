package com.group3.BilliardManagementSystem.ThaiDuyVu.Controller.auth;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.CreateUserRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.UpdateUserRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.UserResponse;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserResponse createUser(
            @RequestBody CreateUserRequest request
    ) {
        return userService.createUser(request);
    }
    @GetMapping("/not-employee")
    public List<UserResponse> getUsersWithoutEmployee() {
        return userService.getUsersWithoutEmployee();
    }
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}