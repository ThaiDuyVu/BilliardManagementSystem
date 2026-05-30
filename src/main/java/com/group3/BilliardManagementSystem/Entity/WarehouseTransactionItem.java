package com.group3.BilliardManagementSystem.Entity;

import com.group3.BilliardManagementSystem.Entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Line item within a WarehouseTransaction.
 * Each item maps to one InventoryItem and one generated StockMovement.
 */
@Entity
@Table(name = "warehouse_transaction_items",
    indexes = {
        @Index(name = "idx_wti_transaction", columnList = "warehouse_transaction_id"),
        @Index(name = "idx_wti_item", columnList = "inventory_item_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseTransactionItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_transaction_id", nullable = false)
    private WarehouseTransaction warehouseTransaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Column(name = "quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit_cost", precision = 15, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal; // quantity * unitCost

    @Column(name = "note", length = 255)
    private String note;

    /** Back-reference to the generated stock movement after approval */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_movement_id")
    private StockMovement stockMovement;
}
