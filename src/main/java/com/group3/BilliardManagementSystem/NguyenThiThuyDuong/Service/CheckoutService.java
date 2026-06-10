package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Service;

import com.group3.BilliardManagementSystem.Entity.Payment;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO.InvoiceResponse;

public interface CheckoutService {
    InvoiceResponse checkout(Long sessionId, Payment.PaymentMethod paymentMethod);
}