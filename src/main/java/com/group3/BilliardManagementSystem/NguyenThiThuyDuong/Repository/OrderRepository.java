package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository;

import com.group3.BilliardManagementSystem.Entity.Order;
import com.group3.BilliardManagementSystem.Entity.PlaySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findBySession(PlaySession session);
}