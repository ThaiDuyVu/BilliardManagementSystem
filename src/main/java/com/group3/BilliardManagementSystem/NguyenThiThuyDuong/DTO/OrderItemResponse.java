package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}