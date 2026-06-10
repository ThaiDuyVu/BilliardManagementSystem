package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Service;

import com.group3.BilliardManagementSystem.Entity.Invoice;

public interface InvoiceService {
    Invoice checkout(Long orderId);
}