package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Controller;

import com.group3.BilliardManagementSystem.Entity.Payment;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO.InvoiceResponse;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    // Định nghĩa DTO nhận dữ liệu ngay tại đây
    public record CheckoutRequest(Payment.PaymentMethod paymentMethod) {}

    @PostMapping("/checkout/{sessionId}")
    public InvoiceResponse checkout(@PathVariable Long sessionId, @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(sessionId, request.paymentMethod());
    }
}