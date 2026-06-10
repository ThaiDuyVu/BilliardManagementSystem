package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Service;

import com.group3.BilliardManagementSystem.Entity.*;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.InvoiceRepository;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final InventoryService inventoryService;

    @Override
    public Invoice checkout(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("Order is empty");
        }

        BigDecimal total = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-" + System.currentTimeMillis())
                .session(order.getSession())
                .issuedAt(LocalDateTime.now())
                .serviceAmount(total)
                .totalAmount(total)
                .status(Invoice.InvoiceStatus.PAID)
                .build();

        invoiceRepository.save(invoice);

        order.setStatus(Order.OrderStatus.CONFIRMED);
        orderRepository.save(order);

        inventoryService.deductStock(order);

        return invoice;
    }
}