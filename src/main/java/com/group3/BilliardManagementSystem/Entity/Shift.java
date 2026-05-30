package com.group3.BilliardManagementSystem.Entity;

import com.group3.BilliardManagementSystem.Entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a named work shift template (e.g., Morning 06:00–14:00).
 * Shift assignments link employees to specific dates using this template.
 */
@Entity
@Table(name = "shifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shift extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name; // e.g., "Morning", "Evening", "Night"

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /** True if shift crosses midnight */
    @Column(name = "overnight", nullable = false)
    @Builder.Default
    private boolean overnight = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ShiftAssignment> assignments = new ArrayList<>();
}
