package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Service;

import com.group3.BilliardManagementSystem.Entity.Order;
import com.group3.BilliardManagementSystem.Entity.PlaySession;

import java.math.BigDecimal;

public interface OrderService {

    /**
     * Create order for a session if not exists
     * Rule: 1 session = 1 order
     */
    Order getOrCreateOrder(PlaySession session);

    /**
     * Add item to order (we will implement OrderItem later)
     */
    Order addItem(Long orderId, Long productId, int quantity);
    Order updateItem(Long orderId, Long productId, int quantity);
    /**
     * Calculate total amount of order
     */
    BigDecimal calculateTotal(Long orderId);
    Order updateIncreaseItem(Long orderId, Long productId);

    Order updateDecreaseItem(Long orderId, Long productId);

    Order removeItem(Long orderId, Long productId);
    /**
     * Confirm order (trigger inventory later - Hoang module)
     */
    Order confirmOrder(Long orderId);
}