package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Controller;

import com.group3.BilliardManagementSystem.Entity.*;
import com.group3.BilliardManagementSystem.MinhChau.repository.PlaySessionRepository;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO.*;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.OrderRepository;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pos/order")
@RequiredArgsConstructor
public class POSOrderController {

    private final PlaySessionRepository sessionRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    // GET ORDER BY SESSION
    @GetMapping("/{sessionId}")
    public OrderResponse getOrder(@PathVariable Long sessionId) {

        PlaySession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Order order = orderService.getOrCreateOrder(session);

        return map(order);
    }

    // ADD ITEM
    @PostMapping("/{orderId}/item")
    public OrderResponse addItem(
            @PathVariable Long orderId,
            @RequestBody AddItemRequest request
    ) {
        Order order = orderService.addItem(
                orderId,
                request.productId(),
                request.quantity()
        );

        return map(order);
    }

    // INCREASE
    @PutMapping("/{orderId}/item/increase")
    public OrderResponse increase(
            @PathVariable Long orderId,
            @RequestParam Long productId
    ) {
        Order order = orderService.updateIncreaseItem(orderId, productId);
        return map(order);
    }

    // DECREASE
    @PutMapping("/{orderId}/item/decrease")
    public OrderResponse decrease(
            @PathVariable Long orderId,
            @RequestParam Long productId
    ) {
        Order order = orderService.updateDecreaseItem(orderId, productId);
        return map(order);
    }

    // REMOVE
    @DeleteMapping("/{orderId}/item")
    public OrderResponse remove(
            @PathVariable Long orderId,
            @RequestParam Long productId
    ) {
        Order order = orderService.removeItem(orderId, productId);
        return map(order);
    }

    // UPDATE (optional)
    @PutMapping("/{orderId}/item")
    public OrderResponse updateItem(
            @PathVariable Long orderId,
            @RequestBody UpdateItemRequest req
    ) {
        Order order = orderService.updateItem(
                orderId,
                req.productId(),
                req.quantity()
        );

        return map(order);
    }

    // MAPPER
    private OrderResponse map(Order order) {

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(i -> new OrderItemResponse(
                                i.getProduct().getId(),
                                i.getProduct().getName(),
                                i.getQuantity(),
                                i.getUnitPrice(),
                                i.getSubtotal()
                        ))
                        .collect(Collectors.toList())
        );
    }
}