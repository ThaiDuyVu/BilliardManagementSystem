package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignShiftRequest {

    private Long employeeId;

    private Long shiftId;

    private LocalDate workDate;

    private String note;
}