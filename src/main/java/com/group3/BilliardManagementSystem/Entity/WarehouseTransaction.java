package com.group3.BilliardManagementSystem.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a warehouse import/export transaction (e.g., purchase order receipt,
 * inter-branch transfer, or stock disposal batch).
 *
 * One WarehouseTransaction contains many WarehouseTransactionItems,
 * each generating a StockMovement. This allows bulk imports to be
 * tracked as a single business event.
 */
@Entity
@Table(name = "warehouse_transactions",
    indexes = {
        @Index(name = "idx_wt_branch", columnList = "branch_id"),
        @Index(name = "idx_wt_type_date", columnList = "transaction_type, transaction_date"),
        @Index(name = "idx_wt_ref_code", columnList = "reference_code")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_code", nullable = false, unique = true, length = 50)
    private String referenceCode; // e.g., "PO-20240701-001"

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    /** Counterpart branch for inter-branch transfers */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counterpart_branch_id")
    private Branch counterpartBranch;

    @Column(name = "supplier_name", length = 100)
    private String supplierName;

    @Column(name = "total_value", precision = 15, scale = 2)
    private BigDecimal totalValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    private User approvedBy;

    @Column(name = "note", length = 500)
    private String note;

    @OneToMany(mappedBy = "warehouseTransaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<WarehouseTransactionItem> items = new ArrayList<>();

    public enum TransactionType {
        IMPORT,        // Stock received from supplier
        EXPORT,        // Stock removed (disposal, return to supplier)
        TRANSFER_OUT,  // Sent to another branch
        TRANSFER_IN,   // Received from another branch
        ADJUSTMENT     // Stock check adjustment batch
    }

    public enum TransactionStatus {
        DRAFT,    // Being built, not committed
        PENDING,  // Submitted for approval
        APPROVED, // Approved — triggers stock movements
        REJECTED, // Rejected by approver
        CANCELLED
    }
}
