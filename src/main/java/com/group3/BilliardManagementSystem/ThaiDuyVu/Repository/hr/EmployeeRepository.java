package com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.hr;

import com.group3.BilliardManagementSystem.Entity.Employee;
import com.group3.BilliardManagementSystem.Entity.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByUserId(Long userId);

    List<Employee> findByBranchId(Long branchId);
    boolean existsByIdAndShiftAssignments_WorkDateAfterAndShiftAssignments_StatusNot(
            Long employeeId,
            LocalDate date,
            ShiftAssignment.AssignmentStatus status
    );
}