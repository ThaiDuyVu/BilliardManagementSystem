package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository;

import com.group3.BilliardManagementSystem.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderIdAndProductId(Long orderId, Long productId);

}