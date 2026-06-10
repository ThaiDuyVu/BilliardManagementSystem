package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr;

import lombok.Data;

import java.time.LocalTime;

@Data
public class CreateShiftRequest {

    private String name;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean overnight;

    private Integer maxEmployees;

    private Long branchId;
}