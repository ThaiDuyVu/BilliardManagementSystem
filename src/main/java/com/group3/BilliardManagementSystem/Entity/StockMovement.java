package com.group3.BilliardManagementSystem.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable ledger record of every stock change for an inventory item.
 *
 * This is the authoritative stock history. currentStock on InventoryItem
 * is derived from applying all movements in order. Movements are:
 *
 * INBOUND  (+qty): stock import, initial setup, return-from-use
 * OUTBOUND (-qty): order fulfillment (event-driven), manual removal
 * ADJUSTMENT: stock check reconciliation (positive or negative delta)
 * WASTE: spoilage or breakage
 *
 * Insert-only: never update or delete movement records.
 * Use ADJUSTMENT type for corrections.
 */
@Entity
@Table(name = "stock_movements",
    indexes = {
        @Index(name = "idx_sm_item", columnList = "inventory_item_id"),
        @Index(name = "idx_sm_movement_time", columnList = "movement_time"),
        @Index(name = "idx_sm_type", columnList = "movement_type"),
        @Index(name = "idx_sm_order", columnList = "order_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    /**
     * Signed quantity change:
     * positive for INBOUND/ADJUSTMENT(+), negative for OUTBOUND/WASTE/ADJUSTMENT(-)
     */
    @Column(name = "quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    /** Stock level snapshot BEFORE this movement */
    @Column(name = "stock_before", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockBefore;

    /** Stock level snapshot AFTER this movement */
    @Column(name = "stock_after", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockAfter;

    @Column(name = "unit_cost", precision = 15, scale = 2)
    private BigDecimal unitCost; // Cost at time of movement for COGS tracking

    @Column(name = "movement_time", nullable = false)
    private LocalDateTime movementTime;

    /** Optional link to the F&B order that triggered this movement */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    /** Optional link to warehouse transaction (for batch imports) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_transaction_id")
    private WarehouseTransaction warehouseTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_user_id")
    private User performedBy;

    @Column(name = "reference_code", length = 100)
    private String referenceCode; // PO number, adjustment ticket, etc.

    @Column(name = "note", length = 255)
    private String note;

    public enum MovementType {
        INBOUND,     // Stock received from supplier
        OUTBOUND,    // Consumed by order (event-driven)
        ADJUSTMENT,  // Manual stock check reconciliation
        WASTE,       // Spoilage, breakage, expiry
        TRANSFER_IN, // Received from another branch
        TRANSFER_OUT // Sent to another branch
    }
}
