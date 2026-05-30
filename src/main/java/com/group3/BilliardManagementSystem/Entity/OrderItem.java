package com.group3.BilliardManagementSystem.Entity;

import com.group3.BilliardManagementSystem.Entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * A single line item within an Order.
 *
 * unitPrice is snapshotted at order time — not referenced from
 * Product.sellingPrice — so price changes don't affect historical orders.
 * This is critical for financial integrity.
 */
@Entity
@Table(name = "order_items",
    indexes = {
        @Index(name = "idx_order_item_order", columnList = "order_id"),
        @Index(name = "idx_order_item_product", columnList = "product_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /** Price snapshotted at the time of order — financial integrity */
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    /** Discount applied to this line item (amount, not percentage) */
    @Column(name = "discount_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal; // = (unitPrice * quantity) - discountAmount

    @Column(name = "note", length = 255)
    private String note; // E.g., "no ice", "extra spicy"
}
