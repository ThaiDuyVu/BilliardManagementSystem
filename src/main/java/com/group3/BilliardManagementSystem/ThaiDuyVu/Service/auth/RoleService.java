package com.group3.BilliardManagementSystem.ThaiDuyVu.Service.auth;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.CreateRoleRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.RoleResponse;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.UpdateRoleRequest;

import java.util.List;

public interface RoleService {

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id);

    RoleResponse createRole(CreateRoleRequest request);

    RoleResponse updateRole(Long id, UpdateRoleRequest request);

    void deleteRole(Long id);
}