package com.group3.BilliardManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physical branch/location of the billiards club.
 * All major resources (tables, staff, inventory) are scoped to a Branch.
 * This is the primary multi-tenancy anchor for the system.
 */
@Entity
@Table(name = "branches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private BranchStatus status = BranchStatus.ACTIVE;

    // Relationships
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    public enum BranchStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
}
