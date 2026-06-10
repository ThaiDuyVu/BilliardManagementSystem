package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Service;

import com.group3.BilliardManagementSystem.Entity.Order;

public interface InventoryService {
    void deductStock(Order order);
}