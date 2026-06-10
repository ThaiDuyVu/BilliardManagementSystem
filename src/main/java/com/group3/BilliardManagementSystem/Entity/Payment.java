package com.group3.BilliardManagementSystem.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Records a payment transaction for an invoice.
 *
 * Supports split payment: one invoice may have multiple Payment records
 * if the customer pays with mixed methods (e.g., cash + card).
 * The sum of all Payment.amountPaid for an invoice must equal invoice.totalAmount
 * for the invoice to be marked PAID.
 *
 * transactionRef: external payment gateway reference (e.g., VNPay, Momo, Stripe).
 */
@Entity
@Table(name = "payments",
    indexes = {
        @Index(name = "idx_payment_invoice", columnList = "invoice_id"),
        @Index(name = "idx_payment_method", columnList = "payment_method"),
        @Index(name = "idx_payment_time", columnList = "payment_time")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "received_by_user_id", nullable = false)
    private User receivedBy;

    @Column(name = "amount_paid", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "change_returned", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal changeReturned = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    /** External gateway reference code (e.g., VNPay transaction ID) */
    @Column(name = "transaction_ref", length = 100)
    private String transactionRef;

    @Column(name = "payment_time", nullable = false)
    private LocalDateTime paymentTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.COMPLETED;

    @Column(name = "note", length = 255)
    private String note;

    public enum PaymentMethod {
        CASH, BANK_TRANSFER, CREDIT_CARD, DEBIT_CARD,
        MOMO, VNPAY, ZALOPAY, VOUCHER, COMPLIMENTARY
    }

    public enum PaymentStatus {
        PENDING,    // Awaiting gateway confirmation
        COMPLETED,  // Successfully processed
        FAILED,     // Transaction declined
        REFUNDED    // Reversed
    }
}
