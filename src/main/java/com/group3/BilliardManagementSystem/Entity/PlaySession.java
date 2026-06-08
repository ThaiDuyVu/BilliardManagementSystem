package com.group3.BilliardManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single playing session on a billiard table.
 *
 * Key invariants:
 * - Only one ACTIVE session per table at any time (enforced by service layer).
 * - actualPlayMinutes excludes paused durations (computed from SessionTimeLogs).
 * - totalPlayAmount is calculated at end: actualPlayMinutes * table.ratePerMinute.
 *
 * State machine:
 *   ACTIVE → PAUSED → ACTIVE → ENDED
 *   ACTIVE → ENDED (direct finish)
 */
@Entity
@Table(name = "play_sessions",
    indexes = {
        @Index(name = "idx_session_table_status", columnList = "table_id, status"),
        @Index(name = "idx_session_start_time", columnList = "start_time"),
        @Index(name = "idx_session_opened_by", columnList = "opened_by_user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaySession extends BaseEntity {

    @Id
    @Column(name = "session_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "table_id", nullable = false)
    private BilliardTable table;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "opened_by_user_id", nullable = false)
    private User openedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_user_id")
    private User closedBy;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * Actual billable play minutes (total elapsed - paused duration).
     * Populated when session ends. Use SessionTimeLogs to derive this.
     */
    @Column(name = "actual_play_minutes")
    private Integer actualPlayMinutes;

    @Column(name = "total_play_amount", precision = 15, scale = 2)
    private BigDecimal totalPlayAmount;

    /** Optional customer name for tracking, not linked to user account */
    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;

    @Column(name = "note", length = 255)
    private String note;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SessionTimeLog> timeLogs = new ArrayList<>();

    /**
     * Pre-fetch for invoice generation — all orders in this session.
     * Avoids N+1 on invoice calculation.
     */
    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    public enum SessionStatus {
        ACTIVE,   // Timer running
        PAUSED,   // Timer stopped temporarily
        ENDED,    // Session complete, awaiting payment
        INVOICED  // Invoice generated and paid
    }
}
