package com.group3.BilliardManagementSystem.ThaiDuyVu.Service.auth;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.CreateRoleRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.RoleResponse;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth.UpdateRoleRequest;
import com.group3.BilliardManagementSystem.Entity.Permission;
import com.group3.BilliardManagementSystem.Entity.Role;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.PermissionRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        return mapToResponse(role);
    }

    @Override
    public RoleResponse createRole(CreateRoleRequest request) {

        Set<Permission> permissions =
                new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(permissions)
                .build();

        return mapToResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse updateRole(Long id, UpdateRoleRequest request) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Permission> permissions =
                new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));

        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setPermissions(permissions);

        return mapToResponse(roleRepository.save(role));
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    private RoleResponse mapToResponse(Role role) {

        Set<String> permissions = role.getPermissions()
                .stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissions)
                .build();
    }
}