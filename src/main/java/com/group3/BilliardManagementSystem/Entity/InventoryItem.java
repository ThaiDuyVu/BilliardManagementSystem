package com.group3.BilliardManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a trackable inventory item (raw material or resalable good).
 *
 * Stock level is maintained by StockMovement records.
 * currentStock is a denormalized field updated on each movement for fast reads.
 * A scheduled job can periodically recompute it from movements to detect drift.
 *
 * Scoped to a branch for multi-branch isolation.
 */
@Entity
@Table(name = "inventory_items",
    indexes = {
        @Index(name = "idx_inv_item_branch", columnList = "branch_id"),
        @Index(name = "idx_inv_item_sku", columnList = "sku"),
        @Index(name = "idx_inv_item_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit; // e.g., "can", "bottle", "kg", "liter"

    /** Denormalized current stock quantity — source of truth for fast reads */
    @Column(name = "current_stock", nullable = false, precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal currentStock = BigDecimal.ZERO;

    /** Alert threshold for low-stock notifications */
    @Column(name = "reorder_level", precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal reorderLevel = BigDecimal.ZERO;

    @Column(name = "cost_price", precision = 15, scale = 2)
    private BigDecimal costPrice; // Average purchase cost per unit

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ItemStatus status = ItemStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @OneToMany(mappedBy = "inventoryItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<StockMovement> movements = new ArrayList<>();

    public enum ItemStatus {
        ACTIVE, INACTIVE, DISCONTINUED
    }
}
