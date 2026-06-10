package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.system;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchResponse {

    private Long id;
    private String name;
}