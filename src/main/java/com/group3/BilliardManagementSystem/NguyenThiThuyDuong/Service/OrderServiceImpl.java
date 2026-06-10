package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Service;

import com.group3.BilliardManagementSystem.Entity.*;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.OrderItemRepository;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.OrderRepository;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.ProductRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    @Override
    @Transactional
    public Order getOrCreateOrder(PlaySession session) {
        return orderRepository.findBySession(session)
                .orElseGet(() -> {

                    Authentication authentication =
                            SecurityContextHolder.getContext().getAuthentication();
                    System.out.println(
                            "CURRENT USER = " + authentication.getName());
                    String username = authentication.getName();

                    User currentUser = userRepository
                            .findByUsername(username)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found: " + username
                                    ));

                    Order order = new Order();

                    order.setSession(session);
                    order.setCreatedBy(currentUser);

                    order.setStatus(Order.OrderStatus.PENDING);
                    order.setTotalAmount(BigDecimal.ZERO);
                    order.setInventoryDeducted(false);
                    order.setItems(new ArrayList<>());

                    return orderRepository.save(order);
                });
    }

    @Override
    @Transactional
    public Order addItem(Long orderId, Long productId, int quantity) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 1. tìm item đã có trong order
        OrderItem existingItem = order.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem == null) {

            BigDecimal subtotal =
                    product.getSellingPrice()
                            .multiply(BigDecimal.valueOf(quantity));

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .quantity(quantity)
                    .unitPrice(product.getSellingPrice())
                    .discountAmount(BigDecimal.ZERO)
                    .subtotal(subtotal)
                    .build();

            // ⚠️ QUAN TRỌNG: persist item riêng
            orderItemRepository.save(item);

            order.getItems().add(item);

        } else {

            existingItem.setQuantity(
                    existingItem.getQuantity() + quantity
            );

            existingItem.setSubtotal(
                    existingItem.getUnitPrice()
                            .multiply(BigDecimal.valueOf(existingItem.getQuantity()))
            );

            // ⚠️ update DB
            orderItemRepository.save(existingItem);
        }

        // 2. recalc total (chuẩn hóa)
        BigDecimal total = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);

        return orderRepository.save(order);
    }
    @Override
    @Transactional
    public Order updateItem(Long orderId, Long productId, int quantity) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderItem item = order.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (quantity <= 0) {
            order.getItems().remove(item);
            orderItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            item.setSubtotal(
                    item.getUnitPrice()
                            .multiply(BigDecimal.valueOf(quantity))
            );
            orderItemRepository.save(item);
        }

        BigDecimal total = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);

        return orderRepository.save(order);
    }
    @Override
    @Transactional
    public Order updateIncreaseItem(Long orderId, Long productId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderItem item = orderItemRepository
                .findByOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setQuantity(item.getQuantity() + 1);

        item.setSubtotal(
                item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))
        );

        orderItemRepository.save(item);

        recalcOrderTotal(order);

        return orderRepository.save(order);
    }
    private void recalcOrderTotal(Order order) {

        BigDecimal total = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
    }
    @Override
    @Transactional
    public Order updateDecreaseItem(Long orderId, Long productId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderItem item = orderItemRepository
                .findByOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        int newQty = item.getQuantity() - 1;

        if (newQty <= 0) {
            order.getItems().remove(item);
            orderItemRepository.delete(item);
        } else {
            item.setQuantity(newQty);
            item.setSubtotal(
                    item.getUnitPrice()
                            .multiply(BigDecimal.valueOf(newQty))
            );
            orderItemRepository.save(item);
        }

        recalcOrderTotal(order);

        return orderRepository.save(order);
    }
    @Override
    @Transactional
    public Order removeItem(Long orderId, Long productId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderItem item = orderItemRepository
                .findByOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        order.getItems().remove(item);

        orderItemRepository.delete(item);

        recalcOrderTotal(order);

        return orderRepository.save(order);
    }
    @Override
    public BigDecimal calculateTotal(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Order confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(Order.OrderStatus.CONFIRMED);
        return orderRepository.save(order);
    }
}