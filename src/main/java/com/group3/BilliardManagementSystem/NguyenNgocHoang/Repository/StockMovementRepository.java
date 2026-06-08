package com.group3.BilliardManagementSystem.NguyenNgocHoang.Repository;

import com.group3.BilliardManagementSystem.Entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

}