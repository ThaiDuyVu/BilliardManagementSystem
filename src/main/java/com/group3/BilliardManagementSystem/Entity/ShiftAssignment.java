package com.group3.BilliardManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Maps a specific employee to a specific shift on a specific date.
 * Enables daily scheduling: "Alice works Morning shift on 2024-07-01".
 */
@Entity
@Table(
        name = "shift_assignments",
        indexes = {
                @Index(
                        name = "idx_shift_assign_employee_date",
                        columnList = "employee_id, work_date"
                ),
                @Index(
                        name = "idx_shift_assign_date",
                        columnList = "work_date"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employee_shift_date",
                        columnNames = {
                                "employee_id",
                                "shift_id",
                                "work_date"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.SCHEDULED;

    @Column(name = "note", length = 255)
    private String note;

    public enum AssignmentStatus {
        SCHEDULED, COMPLETED, ABSENT, SWAPPED, CANCELLED
    }
}
