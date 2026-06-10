package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO;

import java.io.Serializable;

public record UpdateItemRequest(
        Long productId,
        Integer quantity
) implements Serializable {
}