package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth;

import lombok.Data;
import java.util.Set;

@Data
public class CreateRoleRequest {

    private String name;

    private String description;

    private Set<Long> permissionIds;
}