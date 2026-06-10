package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Service;

import com.group3.BilliardManagementSystem.Entity.*;
import com.group3.BilliardManagementSystem.MinhChau.service.PlaySessionService;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO.InvoiceResponse;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.InvoiceRepository;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.OrderRepository;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.PaymentRepository; // Import mới
import com.group3.BilliardManagementSystem.MinhChau.repository.PlaySessionRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final OrderRepository orderRepository;
    private final PlaySessionRepository sessionRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository; // Inject mới
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final PlaySessionService playSessionService;

    @Override
    @Transactional
    public InvoiceResponse checkout(Long sessionId, Payment.PaymentMethod paymentMethod) {

        PlaySession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Order order = orderRepository.findBySession(session)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal serviceAmount = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long seconds = Duration.between(session.getStartTime(), LocalDateTime.now()).getSeconds();
        int minutes = (int) Math.ceil(seconds / 60.0);
        BigDecimal rate = session.getTable().getRatePerMinute();
        BigDecimal playAmount = rate.multiply(BigDecimal.valueOf(minutes));
        BigDecimal total = playAmount.add(serviceAmount);

        // Tạo Invoice
        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-" + System.currentTimeMillis())
                .session(session)
                .issuedBy(user)
                .issuedAt(LocalDateTime.now())
                .playMinutes(minutes)
                .ratePerMinute(rate)
                .playAmount(playAmount)
                .serviceAmount(serviceAmount)
                .totalAmount(total)
                .status(Invoice.InvoiceStatus.PAID) // Đánh dấu đã thanh toán
                .build();
        invoiceRepository.save(invoice);

        // Lưu bản ghi Payment
        Payment payment = Payment.builder()
                .invoice(invoice)
                .receivedBy(user)
                .amountPaid(total)
                .paymentMethod(paymentMethod)
                .paymentTime(LocalDateTime.now())
                .status(Payment.PaymentStatus.COMPLETED)
                .build();
        paymentRepository.save(payment);

        inventoryService.deductStock(order);
        playSessionService.endSession(sessionId);
        order.setStatus(Order.OrderStatus.CONFIRMED);
        orderRepository.save(order);

        return new InvoiceResponse(
                invoice.getId(), invoice.getInvoiceNumber(), session.getId(),
                user.getId(), invoice.getIssuedAt(), invoice.getPlayMinutes(),
                invoice.getRatePerMinute(), invoice.getPlayAmount(),
                invoice.getServiceAmount(), invoice.getTotalAmount(),
                invoice.getStatus().name()
        );
    }
}