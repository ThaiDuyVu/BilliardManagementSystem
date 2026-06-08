package com.group3.BilliardManagementSystem.NguyenNgocHoang.Event;

import com.group3.BilliardManagementSystem.Entity.Order;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderConfirmedEvent {
    private final Order order;
}