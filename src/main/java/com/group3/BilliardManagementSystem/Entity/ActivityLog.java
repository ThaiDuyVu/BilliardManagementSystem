package com.group3.BilliardManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Global activity/audit log written by Spring AOP interceptor.
 * Every significant API action is captured here.
 *
 * Designed for:
 * - Security auditing
 * - AI/analytics processing (structured JSON payload)
 * - Debugging and incident response
 *
 * Use INSERT-only semantics — never update or delete rows.
 */
@Entity
@Table(name = "activity_logs",
    indexes = {
        @Index(name = "idx_activity_log_user", columnList = "user_id"),
        @Index(name = "idx_activity_log_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_activity_log_action_time", columnList = "action_time"),
        @Index(name = "idx_activity_log_branch", columnList = "branch_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // Nullable for anonymous/system actions

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "branch_id")
    private Long branchId;

    /** HTTP method: GET, POST, PUT, DELETE */
    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "endpoint", length = 255)
    private String endpoint;

    /** Domain action name: TABLE_OPEN_SESSION, ORDER_CREATE, etc. */
    @Column(name = "action", nullable = false, length = 100)
    private String action;

    /** Entity type being acted on: PlaySession, Order, etc. */
    @Column(name = "entity_type", length = 100)
    private String entityType;

    @Column(name = "entity_id", length = 50)
    private String entityId;

    /**
     * JSON snapshot of changes (before/after) for audit trail.
     * Stored as TEXT to avoid schema coupling.
     * Future: offload to Elasticsearch or data lake for AI processing.
     */
    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "action_time", nullable = false)
    private LocalDateTime actionTime;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    private ActionResult result;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    public enum ActionResult {
        SUCCESS, FAILURE, UNAUTHORIZED, VALIDATION_ERROR
    }
}
