package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.auth;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class RoleResponse {

    private Long id;

    private String name;

    private String description;

    private Set<String> permissions;
}