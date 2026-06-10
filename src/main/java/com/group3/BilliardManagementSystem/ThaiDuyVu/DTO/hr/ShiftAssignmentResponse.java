package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class ShiftAssignmentResponse {

    private Long id;

    private Long employeeId;

    private String employeeName;

    private Long shiftId;

    private String shiftName;

    private LocalDate workDate;

    private String status;

    private String note;
    private LocalTime shiftStartTime;

    private LocalTime shiftEndTime;
}