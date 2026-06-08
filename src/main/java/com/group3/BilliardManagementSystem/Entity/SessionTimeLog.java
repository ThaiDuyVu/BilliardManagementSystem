package com.group3.BilliardManagementSystem.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Immutable log of every pause and resume event within a session.
 *
 * Design rationale: storing segment boundaries (not just total pause time)
 * provides a complete audit trail and enables accurate billing recalculation
 * if the system clock drifts or session data is manually adjusted.
 *
 * Actual play time = sum(resume_time[i] - pause_time[i]) for all intervals.
 * Use INSERT-only semantics — rows are never updated after creation.
 *
 * Example:
 *   START  10:00 → LOG: type=SESSION_START
 *   PAUSE  10:30 → LOG: type=PAUSE
 *   RESUME 10:45 → LOG: type=RESUME  (paused 15 min)
 *   END    11:30 → LOG: type=SESSION_END (played 45+45=75 min net)
 */
@Entity
@Table(name = "session_time_logs",
    indexes = {
        @Index(name = "idx_stl_session", columnList = "session_id"),
        @Index(name = "idx_stl_event_time", columnList = "event_time")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionTimeLog extends BaseEntity {

    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private PlaySession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    /** Duration of this segment in seconds — populated on RESUME and SESSION_END */
    @Column(name = "segment_seconds")
    private Long segmentSeconds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggered_by_user_id")
    private User triggeredBy;

    @Column(name = "note", length = 255)
    private String note;

    public enum EventType {
        SESSION_START,
        PAUSE,
        RESUME,
        SESSION_END
    }
}
