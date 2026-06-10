package com.group3.BilliardManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Outbox pattern table for reliable event publishing.
 *
 * Instead of publishing domain events directly to a message broker
 * (which risks dual-write failures), events are written to this table
 * within the same DB transaction as the domain change.
 *
 * A background relay process polls PENDING events and publishes them
 * to the message broker (Kafka, RabbitMQ, Redis Streams).
 *
 * This makes the system:
 * - Microservices-ready: events can trigger external consumers
 * - AI-ready: events form a complete audit timeline
 * - Resilient: no event loss even if the broker is temporarily down
 *
 * Events to publish:
 *   SESSION_STARTED, SESSION_PAUSED, SESSION_RESUMED, SESSION_ENDED
 *   ORDER_CONFIRMED, ORDER_CANCELLED
 *   STOCK_LOW_ALERT, WAREHOUSE_TRANSACTION_APPROVED
 *   PAYMENT_RECEIVED
 */
@Entity
@Table(name = "domain_event_logs",
    indexes = {
        @Index(name = "idx_del_status_created", columnList = "status, created_at"),
        @Index(name = "idx_del_aggregate", columnList = "aggregate_type, aggregate_id"),
        @Index(name = "idx_del_event_type", columnList = "event_type")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Domain event name: SESSION_ENDED, ORDER_CONFIRMED, etc. */
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    /** Entity type the event occurred on: PlaySession, Order, etc. */
    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 50)
    private String aggregateId;

    /** Full event payload as JSON for broker publishing */
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private EventStatus status = EventStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    public enum EventStatus {
        PENDING,   // Waiting for relay
        PUBLISHED, // Successfully sent to broker
        FAILED     // Max retries exceeded
    }
}
