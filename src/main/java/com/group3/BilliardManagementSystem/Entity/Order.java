package com.group3.BilliardManagementSystem.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * An F&B service order placed during a session.
 *
 * Multiple orders can belong to the same session (e.g., customer
 * reorders mid-game). Each order has a status lifecycle.
 *
 * On CONFIRMED: trigger inventory deduction event (event-driven).
 * On CANCELLED: reverse any already-deducted stock.
 */
@Entity
@Table(name = "orders",
    indexes = {
        @Index(name = "idx_order_session", columnList = "session_id"),
        @Index(name = "idx_order_status", columnList = "status"),
        @Index(name = "idx_order_created_by", columnList = "created_by_user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private PlaySession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by_user_id")
    private User confirmedBy;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "note", length = 255)
    private String note;

    /** True once inventory has been deducted for this order */
    @Column(name = "inventory_deducted", nullable = false)
    @Builder.Default
    private boolean inventoryDeducted = false;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    public enum OrderStatus {
        PENDING,    // Just created, not yet confirmed by kitchen/bar
        CONFIRMED,  // Accepted — triggers inventory deduction
        SERVING,    // Being prepared / in service
        DELIVERED,  // Handed to customer
        CANCELLED   // Voided — reverse any deductions
    }
}
