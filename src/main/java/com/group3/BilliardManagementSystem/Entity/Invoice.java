package com.group3.BilliardManagementSystem.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Final invoice combining play session charges and F&B order charges.
 *
 * Invoice is generated once the session ends. The invoice consolidates:
 * - playAmount: calculated from actual play time * table rate
 * - serviceAmount: sum of all confirmed order totals
 * - discountAmount: any manual or promotion discounts
 * - taxAmount: computed tax
 * - totalAmount: the final payable amount
 *
 * Once PAID, the invoice is immutable.
 */
@Entity
@Table(name = "invoices",
    indexes = {
        @Index(name = "idx_invoice_session", columnList = "session_id"),
        @Index(name = "idx_invoice_number", columnList = "invoice_number"),
        @Index(name = "idx_invoice_status", columnList = "status"),
        @Index(name = "idx_invoice_issued_at", columnList = "issued_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Human-readable sequential number: INV-20240701-0001 */
    @Column(name = "invoice_number", nullable = false, unique = true, length = 30)
    private String invoiceNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private PlaySession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issued_by_user_id", nullable = false)
    private User issuedBy;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    /** Billable minutes billed (from session.actualPlayMinutes) */
    @Column(name = "play_minutes", nullable = false)
    private Integer playMinutes;

    @Column(name = "rate_per_minute", nullable = false, precision = 10, scale = 2)
    private BigDecimal ratePerMinute;

    @Column(name = "play_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal playAmount;

    @Column(name = "service_amount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal serviceAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.PENDING;

    @Column(name = "note", length = 500)
    private String note;

    /** Link to payment once settled */
    @OneToOne(mappedBy = "invoice", fetch = FetchType.LAZY)
    private Payment payment;

    public enum InvoiceStatus {
        PENDING,  // Generated, awaiting payment
        PAID,     // Payment received
        VOID,     // Cancelled/voided
        REFUNDED  // Payment reversed
    }
}
