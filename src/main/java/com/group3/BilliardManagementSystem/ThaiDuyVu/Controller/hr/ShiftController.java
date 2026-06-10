package com.group3.BilliardManagementSystem.ThaiDuyVu.Controller.hr;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr.*;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Service.hr.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    // =========================
    // CREATE SHIFT
    // =========================

    @PostMapping
    public ShiftResponse createShift(
            @RequestBody CreateShiftRequest request
    ) {
        System.out.println("=== CREATE SHIFT CALLED ===");

        return shiftService.createShift(request);
    }

    // =========================
    // GET SHIFTS BY BRANCH
    // =========================

    @GetMapping("/branch/{branchId}")
    public List<ShiftResponse> getShiftsByBranch(
            @PathVariable Long branchId
    ) {
        return shiftService.getShiftsByBranch(branchId);
    }

    // =========================
    // ASSIGN 1 EMPLOYEE
    // =========================

    @PostMapping("/assign")
    public ShiftAssignmentResponse assignEmployee(
            @RequestBody AssignShiftRequest request
    ) {
        return shiftService.assignEmployeeToShift(request);
    }

    // =========================
    // ASSIGN MULTIPLE EMPLOYEES
    // =========================

    @PostMapping("/assign-multiple")
    public List<ShiftAssignmentResponse> assignMultipleEmployees(
            @RequestBody AssignMultipleShiftRequest request
    ) {
        return shiftService.assignMultipleEmployees(request);
    }

    // =========================
    // CANCEL ASSIGNMENT
    // =========================

    @PutMapping("/assignment/{assignmentId}/cancel")
    public String cancelAssignment(
            @PathVariable Long assignmentId
    ) {

        shiftService.cancelAssignment(assignmentId);

        return "Assignment cancelled successfully";
    }

    // =========================
    // VIEW SCHEDULE BY DATE
    // =========================

    @GetMapping("/schedule")
    public List<ShiftAssignmentResponse> getSchedule(
            @RequestParam Long branchId,
            @RequestParam String date
    ) {
        return shiftService.getScheduleByDate(
                branchId,
                LocalDate.parse(date)
        );
    }
    @GetMapping("/schedule/week")
    public List<ShiftAssignmentResponse> getWeeklySchedule(
            @RequestParam Long branchId,
            @RequestParam String startDate
    ) {

        return shiftService.getScheduleByWeek(
                branchId,
                LocalDate.parse(startDate)
        );
    }
    // =========================
    // EMPLOYEE SCHEDULE
    // =========================

    @GetMapping("/employee/{employeeId}")
    public List<ShiftAssignmentResponse> getEmployeeSchedule(
            @PathVariable Long employeeId
    ) {
        return shiftService.getEmployeeSchedule(employeeId);
    }
    @PutMapping("/assignment/{id}")
    public ShiftAssignmentResponse updateAssignment(
            @PathVariable Long id,
            @RequestBody UpdateShiftAssignmentRequest request
    ) {
        return shiftService.updateAssignment(id, request);
    }
}