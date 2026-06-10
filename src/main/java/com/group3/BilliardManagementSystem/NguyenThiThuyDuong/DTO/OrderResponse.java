package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long orderId,
        BigDecimal totalAmount,
        List<OrderItemResponse> items
) {}