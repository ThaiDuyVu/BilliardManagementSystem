package com.group3.BilliardManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tracks actual employee check-in and check-out times.
 * Linked to ShiftAssignment for variance analysis (late/early).
 * Supports biometric or manual check-in modes.
 */
@Entity
@Table(name = "attendance",
    indexes = {
        @Index(name = "idx_attendance_employee_date", columnList = "employee_id, check_in_time"),
        @Index(name = "idx_attendance_branch", columnList = "branch_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    /** Optional link to shift assignment for schedule vs actual comparison */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_assignment_id")
    private ShiftAssignment shiftAssignment;

    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_in_method", nullable = false)
    @Builder.Default
    private CheckMethod checkInMethod = CheckMethod.MANUAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_out_method")
    private CheckMethod checkOutMethod;

    @Column(name = "note", length = 255)
    private String note;

    /** Computed working minutes — nullable until check-out */
    @Column(name = "worked_minutes")
    private Integer workedMinutes;

    public enum CheckMethod {
        MANUAL, QR_CODE, BIOMETRIC, PIN
    }
}
