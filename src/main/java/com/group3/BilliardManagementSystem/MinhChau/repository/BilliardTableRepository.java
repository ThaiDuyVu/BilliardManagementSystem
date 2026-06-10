package com.group3.BilliardManagementSystem.MinhChau.repository;

import com.group3.BilliardManagementSystem.Entity.BilliardTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BilliardTableRepository
        extends JpaRepository<BilliardTable, Long> {
}