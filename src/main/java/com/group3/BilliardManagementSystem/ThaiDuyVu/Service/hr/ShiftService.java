package com.group3.BilliardManagementSystem.ThaiDuyVu.Service.hr;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr.*;

import java.time.LocalDate;
import java.util.List;

public interface ShiftService {

    ShiftResponse createShift(
            CreateShiftRequest request
    );

    List<ShiftResponse> getShiftsByBranch(
            Long branchId
    );

    ShiftAssignmentResponse assignEmployeeToShift(
            AssignShiftRequest request
    );

    List<ShiftAssignmentResponse> getScheduleByDate(
            Long branchId,
            LocalDate workDate
    );
    List<ShiftAssignmentResponse> assignMultipleEmployees(
            AssignMultipleShiftRequest request
    );
    List<ShiftAssignmentResponse> getEmployeeSchedule(
            Long employeeId
    );
    void cancelAssignment(Long assignmentId);
    ShiftAssignmentResponse updateAssignment(
            Long assignmentId,
            UpdateShiftAssignmentRequest request
    );
    List<ShiftAssignmentResponse> getScheduleByWeek(
            Long branchId,
            LocalDate startDate
    );
}