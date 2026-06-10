package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository;

import com.group3.BilliardManagementSystem.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Các hàm cơ bản (save, findById, delete) đã được kế thừa từ JpaRepository
}