package com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.hr;

import com.group3.BilliardManagementSystem.Entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByBranchId(Long branchId);

    List<Shift> findByBranchIdAndActiveTrue(Long branchId);
    boolean existsByBranchIdAndName(
            Long branchId,
            String name
    );

}