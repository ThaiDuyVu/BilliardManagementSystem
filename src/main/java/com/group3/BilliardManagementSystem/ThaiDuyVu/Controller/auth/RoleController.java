package com.group3.BilliardManagementSystem.ThaiDuyVu.Controller.auth;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.CreateRoleRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.RoleResponse;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.UpdateRoleRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Service.auth.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public List<RoleResponse> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public RoleResponse getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @PostMapping
    public RoleResponse createRole(@RequestBody CreateRoleRequest request) {
        return roleService.createRole(request);
    }

    @PutMapping("/{id}")
    public RoleResponse updateRole(
            @PathVariable Long id,
            @RequestBody UpdateRoleRequest request
    ) {
        return roleService.updateRole(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }
}