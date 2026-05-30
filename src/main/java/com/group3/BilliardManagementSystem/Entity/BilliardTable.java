package com.group3.BilliardManagementSystem.Entity;

import com.group3.BilliardManagementSystem.Entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physical billiard table in a branch.
 *
 * Pricing model: per-minute rate allows flexible calculation.
 * Tables can have different rates (e.g., VIP vs standard).
 * Status is managed by the session lifecycle — do not update
 * status directly; let PlaySession state transitions drive it.
 */
@Entity
@Table(name = "billiard_tables",
    indexes = {
        @Index(name = "idx_table_branch_status", columnList = "branch_id, status"),
        @Index(name = "idx_table_code", columnList = "table_code")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BilliardTable extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_code", nullable = false, length = 20)
    private String tableCode; // e.g., "T01", "VIP-01"

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "table_type", nullable = false)
    private TableType tableType;

    /**
     * Base rate per minute in local currency.
     * Invoice calculation: playMinutes * ratePerMinute.
     */
    @Column(name = "rate_per_minute", nullable = false, precision = 10, scale = 2)
    private BigDecimal ratePerMinute;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TableStatus status = TableStatus.AVAILABLE;

    @Column(name = "position_x")
    private Integer positionX; // For frontend layout rendering

    @Column(name = "position_y")
    private Integer positionY;

    @Column(name = "note", length = 255)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PlaySession> sessions = new ArrayList<>();

    public enum TableType {
        POOL, SNOOKER, CAROM, VIP_POOL, VIP_SNOOKER
    }

    public enum TableStatus {
        AVAILABLE,  // No active session
        OCCUPIED,   // Active session running
        PAUSED,     // Session paused
        RESERVED,   // Reserved for upcoming booking
        MAINTENANCE // Out of service
    }
}
