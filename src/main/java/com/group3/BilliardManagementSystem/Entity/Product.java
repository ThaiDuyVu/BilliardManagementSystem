package com.group3.BilliardManagementSystem.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * A sellable F&B item on the menu.
 *
 * Linked to an InventoryItem to enable automatic stock deduction
 * when an order is confirmed. stockDeductionQty specifies how much
 * inventory is consumed per unit sold.
 *
 * Examples:
 *   Product: "Coca-Cola 330ml"  → InventoryItem: "Coca-Cola Can" (qty: 1)
 *   Product: "Coffee"           → InventoryItem: "Coffee Beans" (qty: 0.02 kg)
 */
@Entity
@Table(name = "products",
    indexes = {
        @Index(name = "idx_product_category", columnList = "category_id"),
        @Index(name = "idx_product_sku", columnList = "sku"),
        @Index(name = "idx_product_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku", unique = true, length = 50)
    private String sku;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "selling_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Optional link to inventory for auto-deduction on order confirmation.
     * Null = product has no tracked inventory (e.g., service items).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id")
    private InventoryItem inventoryItem;

    /** How many inventory units are consumed per 1 unit sold */
    @Column(name = "stock_deduction_qty", precision = 10, scale = 3)
    @Builder.Default
    private BigDecimal stockDeductionQty = BigDecimal.ONE;

    public enum ProductStatus {
        AVAILABLE,       // In menu, can be ordered
        OUT_OF_STOCK,    // Temporarily unavailable
        DISCONTINUED,    // Removed from menu permanently
        HIDDEN           // Admin-hidden (not shown to staff)
    }
}
