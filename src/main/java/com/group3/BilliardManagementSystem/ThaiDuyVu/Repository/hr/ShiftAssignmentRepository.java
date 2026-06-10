package com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.hr;

import com.group3.BilliardManagementSystem.Entity.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ShiftAssignmentRepository
        extends JpaRepository<ShiftAssignment, Long> {

    List<ShiftAssignment> findByWorkDate(LocalDate workDate);

    List<ShiftAssignment> findByEmployeeId(Long employeeId);

    List<ShiftAssignment> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    List<ShiftAssignment> findByShiftIdAndWorkDate(Long shiftId, LocalDate workDate);

    List<ShiftAssignment> findByShiftBranchIdAndWorkDate(Long branchId, LocalDate workDate);

    boolean existsByEmployeeIdAndShiftIdAndWorkDate(Long employeeId, Long shiftId, LocalDate workDate);

    boolean existsByEmployeeIdAndWorkDateAndStatusNot(
            Long employeeId,
            LocalDate workDate,
            ShiftAssignment.AssignmentStatus status
    );

    boolean existsByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    long countByShiftIdAndWorkDate(Long shiftId, LocalDate workDate);

    long countByShiftIdAndWorkDateAndStatusNot(
            Long shiftId,
            LocalDate workDate,
            ShiftAssignment.AssignmentStatus status
    );
    boolean existsByEmployeeIdAndWorkDateAfterAndStatusNot(
            Long employeeId,
            LocalDate date,
            ShiftAssignment.AssignmentStatus status
    );
    List<ShiftAssignment> findByShiftBranchIdAndWorkDateBetween(
            Long branchId,
            LocalDate startDate,
            LocalDate endDate
    );
}
