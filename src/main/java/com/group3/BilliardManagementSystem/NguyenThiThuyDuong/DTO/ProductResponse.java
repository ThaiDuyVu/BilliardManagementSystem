package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String imageUrl,
        BigDecimal sellingPrice
) {}