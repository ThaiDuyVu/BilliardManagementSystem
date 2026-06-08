package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AssignMultipleShiftRequest {

    private List<Long> employeeIds;

    private Long shiftId;

    private LocalDate workDate;

    private String note;
}