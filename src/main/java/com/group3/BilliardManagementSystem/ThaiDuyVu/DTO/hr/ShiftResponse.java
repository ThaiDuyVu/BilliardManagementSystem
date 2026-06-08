package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class ShiftResponse {

    private Long id;

    private String name;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean overnight;

    private Boolean active;

    private Integer maxEmployees;

    private Long branchId;

    private String branchName;
}