package com.group3.BilliardManagementSystem.ThaiDuyVu.Service.hr;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr.*;
import com.group3.BilliardManagementSystem.Entity.Branch;
import com.group3.BilliardManagementSystem.Entity.Employee;
import com.group3.BilliardManagementSystem.Entity.Shift;
import com.group3.BilliardManagementSystem.Entity.ShiftAssignment;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.hr.EmployeeRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.hr.ShiftAssignmentRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.hr.ShiftRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.system.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;

    // =========================
    // CREATE SHIFT
    // =========================
    @Override
    public ShiftResponse createShift(CreateShiftRequest request) {

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        if (shiftRepository.existsByBranchIdAndName(branch.getId(), request.getName())) {
            throw new RuntimeException("Shift name already exists in this branch");
        }
        if (
                request.getStartTime()
                        .equals(request.getEndTime())
        ) {

            throw new RuntimeException(
                    "Start time and end time cannot be equal"
            );
        }
        if (request.getMaxEmployees() != null && request.getMaxEmployees() <= 0) {
            throw new RuntimeException("maxEmployees must be > 0");
        }
        boolean overnight =
                request.getEndTime()
                        .isBefore(
                                request.getStartTime()
                        );
        Shift shift = Shift.builder()
                .name(request.getName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .overnight(overnight)
                .maxEmployees(request.getMaxEmployees())
                .branch(branch)
                .build();

        shiftRepository.save(shift);

        return mapToShiftResponse(shift);
    }

    // =========================
    // GET ALL SHIFTS
    // =========================
    @Override
    public List<ShiftResponse> getShiftsByBranch(Long branchId) {

        return shiftRepository
                .findByBranchIdAndActiveTrue(branchId)
                .stream()
                .map(this::mapToShiftResponse)
                .collect(Collectors.toList());
    }

    // =========================
    // ASSIGN EMPLOYEE TO SHIFT
    // =========================
    @Override
    public ShiftAssignmentResponse assignEmployeeToShift(AssignShiftRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (
                employee.getStatus()
                        == Employee.EmployeeStatus.TERMINATED

                        ||

                        employee.getStatus()
                                == Employee.EmployeeStatus.SUSPENDED
        ) {

            throw new RuntimeException(
                    "Employee cannot be assigned"
            );
        }
        if (
                employee.getStatus()
                        != Employee.EmployeeStatus.ACTIVE
        ) {

            throw new RuntimeException(
                    "Employee is not active"
            );
        }
        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new RuntimeException("Shift not found"));
        if (!employee.getBranch().getId()
                .equals(shift.getBranch().getId())) {

            throw new RuntimeException(
                    "Employee and shift belong to different branches"
            );
        }
        if (!shift.isActive()) {
            throw new RuntimeException("Shift is not active");
        }

        LocalDate workDate = request.getWorkDate();
        if (workDate.isBefore(LocalDate.now())) {
            throw new RuntimeException(
                    "Cannot assign shift in the past"
            );
        }
        // duplicate check
        if (
                shiftAssignmentRepository
                        .existsByEmployeeIdAndWorkDateAndStatusNot(
                                employee.getId(),
                                workDate,
                                ShiftAssignment.AssignmentStatus.CANCELLED
                        )
        ) {

            throw new RuntimeException(
                    "Employee already has a shift on this date"
            );
        }

        // max employees check
        long count =
                shiftAssignmentRepository
                        .countByShiftIdAndWorkDateAndStatusNot(
                                shift.getId(),
                                workDate,
                                ShiftAssignment.AssignmentStatus.CANCELLED
                        );

        if (shift.getMaxEmployees() != null && count >= shift.getMaxEmployees()) {
            throw new RuntimeException("Shift is full");
        }

        ShiftAssignment assignment = ShiftAssignment.builder()
                .employee(employee)
                .shift(shift)
                .workDate(workDate)
                .status(ShiftAssignment.AssignmentStatus.SCHEDULED)
                .createdBy(null) // sau này lấy từ SecurityContext
                .note(request.getNote())
                .build();

        shiftAssignmentRepository.save(assignment);

        return mapToShiftAssignmentResponse(assignment);
    }

    // =========================
    // GET SCHEDULE BY DATE
    // =========================
    @Override
    public List<ShiftAssignmentResponse> getScheduleByDate(
            Long branchId,
            LocalDate workDate
    ) {

        return shiftAssignmentRepository
                .findByShiftBranchIdAndWorkDate(
                        branchId,
                        workDate
                )
                .stream()

                .sorted(
                        Comparator.comparing(
                                a -> a.getShift().getStartTime()
                        )
                )

                .map(this::mapToShiftAssignmentResponse)

                .collect(Collectors.toList());
    }
    @Override
    public List<ShiftAssignmentResponse> assignMultipleEmployees(
            AssignMultipleShiftRequest request
    ) {

        Shift shift = shiftRepository.findById(
                request.getShiftId()
        ).orElseThrow(
                () -> new RuntimeException("Shift not found")
        );

        if (!shift.isActive()) {
            throw new RuntimeException("Shift is not active");
        }

        LocalDate workDate = request.getWorkDate();

        if (workDate.isBefore(LocalDate.now())) {
            throw new RuntimeException(
                    "Cannot assign shift in the past"
            );
        }

        long currentCount =
                shiftAssignmentRepository
                        .countByShiftIdAndWorkDateAndStatusNot(
                                shift.getId(),
                                workDate,
                                ShiftAssignment.AssignmentStatus.CANCELLED
                        );

        List<ShiftAssignmentResponse> result =
                new ArrayList<>();

        for (Long employeeId : request.getEmployeeIds()) {

            // Shift đã đầy
            if (
                    shift.getMaxEmployees() != null
                            &&
                            currentCount >= shift.getMaxEmployees()
            ) {
                break;
            }

            Employee employee =
                    employeeRepository.findById(employeeId)
                            .orElse(null);

            if (employee == null) {
                continue;
            }

            // TERMINATED hoặc SUSPENDED
            if (
                    employee.getStatus()
                            == Employee.EmployeeStatus.TERMINATED
                            ||
                            employee.getStatus()
                                    == Employee.EmployeeStatus.SUSPENDED
            ) {
                continue;
            }

            // Chỉ ACTIVE mới được phân
            if (
                    employee.getStatus()
                            != Employee.EmployeeStatus.ACTIVE
            ) {
                continue;
            }

            // Khác chi nhánh
            if (
                    !employee.getBranch().getId()
                            .equals(
                                    shift.getBranch().getId()
                            )
            ) {
                continue;
            }

            // Đã được phân đúng ca này trong ngày này
            if (
                    shiftAssignmentRepository
                            .existsByEmployeeIdAndWorkDateAndStatusNot(
                                    employee.getId(),
                                    workDate,
                                    ShiftAssignment.AssignmentStatus.CANCELLED
                            )
            )
            {
                continue;
            }

            ShiftAssignment assignment =
                    ShiftAssignment.builder()
                            .employee(employee)
                            .shift(shift)
                            .workDate(workDate)
                            .status(
                                    ShiftAssignment.AssignmentStatus.SCHEDULED
                            )
                            .createdBy(null)
                            .note(request.getNote())
                            .build();

            shiftAssignmentRepository.save(
                    assignment
            );

            currentCount++;

            result.add(
                    mapToShiftAssignmentResponse(
                            assignment
                    )
            );
        }

        return result;
    }
    // =========================
    // GET EMPLOYEE SCHEDULE
    // =========================
    @Override
    public List<ShiftAssignmentResponse> getEmployeeSchedule(Long employeeId) {

        return shiftAssignmentRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToShiftAssignmentResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<ShiftAssignmentResponse> getScheduleByWeek(
            Long branchId,
            LocalDate startDate
    ) {

        LocalDate endDate = startDate.plusDays(6);

        return shiftAssignmentRepository
                .findByShiftBranchIdAndWorkDateBetween(
                        branchId,
                        startDate,
                        endDate
                )
                .stream()
                .map(this::mapToShiftAssignmentResponse)
                .toList();
    }
    // =========================
    // MAPPERS
    // =========================

    private ShiftResponse mapToShiftResponse(Shift shift) {
        return ShiftResponse.builder()
                .id(shift.getId())
                .name(shift.getName())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .overnight(shift.isOvernight())
                .active(shift.isActive())
                .maxEmployees(shift.getMaxEmployees())
                .branchId(shift.getBranch().getId())
                .build();
    }

    private ShiftAssignmentResponse mapToShiftAssignmentResponse(ShiftAssignment a) {
        return ShiftAssignmentResponse.builder()
                .id(a.getId())
                .employeeId(a.getEmployee().getId())
                .employeeName(a.getEmployee().getFullName())
                .shiftId(a.getShift().getId())
                .shiftName(a.getShift().getName())
                .workDate(a.getWorkDate())
                .status(a.getStatus().name())
                .note(a.getNote())
                .shiftStartTime(
                        a.getShift().getStartTime()
                )
                .shiftEndTime(
                        a.getShift().getEndTime()
                )
                .build();
    }
    public void cancelAssignment(Long assignmentId)
    {
        ShiftAssignment assignment =
                shiftAssignmentRepository
                        .findById(assignmentId)
                        .orElseThrow(
                                () -> new RuntimeException("Assignment not found")
                        );

        assignment.setStatus(
                ShiftAssignment.AssignmentStatus.CANCELLED
        );

        shiftAssignmentRepository.save(
                assignment
        );
    }
    @Override
    public ShiftAssignmentResponse updateAssignment(
            Long assignmentId,
            UpdateShiftAssignmentRequest request
    ) {

        ShiftAssignment assignment =
                shiftAssignmentRepository
                        .findById(assignmentId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Assignment not found"
                                )
                        );

        if (
                assignment.getStatus()
                        == ShiftAssignment.AssignmentStatus.CANCELLED
        ) {
            throw new RuntimeException(
                    "Cannot update cancelled assignment"
            );
        }

        Shift newShift =
                shiftRepository.findById(
                                request.getShiftId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Shift not found"
                                )
                        );

        Employee employee =
                assignment.getEmployee();

        // khác chi nhánh

        if (
                !employee.getBranch().getId()
                        .equals(
                                newShift.getBranch().getId()
                        )
        ) {
            throw new RuntimeException(
                    "Employee and shift belong to different branches"
            );
        }

        LocalDate newWorkDate =
                request.getWorkDate();

        if (
                newWorkDate.isBefore(
                        LocalDate.now()
                )
        ) {
            throw new RuntimeException(
                    "Cannot assign shift in the past"
            );
        }

        // kiểm tra nhân viên đã có ca khác chưa

        boolean alreadyAssigned =
                shiftAssignmentRepository
                        .existsByEmployeeIdAndWorkDateAndStatusNot(
                                employee.getId(),
                                newWorkDate,
                                ShiftAssignment.AssignmentStatus.CANCELLED
                        );

        boolean sameAssignment =
                assignment.getWorkDate().equals(newWorkDate);

        if (
                alreadyAssigned
                        &&
                        !sameAssignment
        ) {
            throw new RuntimeException(
                    "Employee already has a shift on this date"
            );
        }

        // kiểm tra sức chứa ca

        long currentCount =
                shiftAssignmentRepository
                        .countByShiftIdAndWorkDateAndStatusNot(
                                newShift.getId(),
                                newWorkDate,
                                ShiftAssignment.AssignmentStatus.CANCELLED
                        );

        boolean changingShift =
                !assignment.getShift().getId()
                        .equals(newShift.getId());

        if (
                changingShift
                        &&
                        newShift.getMaxEmployees() != null
                        &&
                        currentCount >= newShift.getMaxEmployees()
        ) {
            throw new RuntimeException(
                    "Target shift is full"
            );
        }

        assignment.setShift(
                newShift
        );

        assignment.setWorkDate(
                newWorkDate
        );

        assignment.setNote(
                request.getNote()
        );

        shiftAssignmentRepository.save(
                assignment
        );

        return mapToShiftAssignmentResponse(
                assignment
        );
    }
}