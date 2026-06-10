package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO;

import com.group3.BilliardManagementSystem.Entity.Payment;

public record CheckoutRequest(Payment.PaymentMethod paymentMethod) {}